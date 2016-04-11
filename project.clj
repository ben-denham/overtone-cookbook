(defproject overtone-cookbook "0.1.0"
  :description "Overtone Cookbook"
  :url "https://ben-denham.github.io/overtone-cookbook"
  :license {:name "GNU General Public License v3.0"
            :url "http://www.gnu.org/licenses/gpl-3.0.txt"}
  :dependencies [[org.clojure/clojure "1.8.0"]
                 [overtone "0.9.1"]
                 [quil "2.3.0"]]
  :plugins [[lein-exec "0.3.6"]]
  ;; See: https://github.com/technomancy/leiningen/pull/1230
  :jvm-opts ^:replace []
  ;; Load core.clj whenever a repl session starts.
  :main overtone-cookbook.core)
