(ns leiningen.gen-site
  (:require [clojure.string :as s]
            [leiningen.exec :refer [deps]]
            [clojure.java.io :as io])
  (:import [java.io File]))

(deps '[[marginalia "0.9.0"]])
(require 'marginalia.parser 'marginalia.html 'marginalia.core)

(def cookbook-files ["src/overtone_cookbook/core.clj"
                     "src/overtone_cookbook/installation.clj"
                     "src/overtone_cookbook/getting-started.clj"])
(def site-dir "site")

(def base-marg-args (concat ["-d" site-dir] cookbook-files))
(def single-file-marg-args (concat ["-f" "index.html"] base-marg-args))
(def multi-file-marg-args (concat ["-m"] base-marg-args))

;; Monkey-patch the marginalia parser to not "require" each namespace,
;; which causes overtone to be loaded.
(intern 'marginalia.parser 'extract-common-docstring
        (fn [form raw nspace-sym]
          (let [sym (second form)]
            (if (symbol? sym)
              (let [maybe-metadocstring (:doc (meta sym))]
                (let [nspace (find-ns sym)
                      [maybe-ds remainder] (let [[_ _ ? & more?] form] [? more?])
                      docstring (if (and (string? maybe-ds) remainder)
                                  maybe-ds
                                  (if (= (first form) 'ns)
                                    (if (not maybe-metadocstring)
                                      (when (string? maybe-ds) maybe-ds)
                                      maybe-metadocstring)
                                    (if-let [ds maybe-metadocstring]
                                      ds
                                      (when nspace
                                        (-> nspace meta :doc)
                                        (marginalia.parser/get-var-docstring nspace-sym sym)))))]
                  [docstring
                   (marginalia.parser/strip-docstring docstring raw)
                   (if (or (= 'ns (first form)) nspace) sym nspace-sym)]))
              [nil raw nspace-sym]))))

(defn build-path [& parts]
  "Join path parts with the file separator to produce a relative
  path."
  (s/join (File/separator) parts))

(defn recursive-delete [file]
  (when (.isDirectory file)
    (doseq [sub-file (.listFiles file)]
      (recursive-delete (io/file sub-file))))
  (io/delete-file file))

(defn gen-site [multi-file?]
  "Re-generate the site html files in site-dir. Can be configured to
  generate a single file, or one file per namespace."
  (println (str "Deleting " site-dir "/"))
  (recursive-delete (io/file site-dir))
  (let [marg-args (if multi-file? multi-file-marg-args single-file-marg-args)]
    ;; Generate the site files.
    (binding [marginalia.html/*resources* ""]
      (marginalia.core/run-marginalia marg-args))
    (when multi-file?
      (println "Renaming toc.html to index.html")
      (let [toc-file (build-path site-dir "toc.html")
            index-file (build-path site-dir "index.html")]
        (.renameTo (io/file toc-file) (io/file index-file))))))

;; Generate a single-page site.
(gen-site false)
