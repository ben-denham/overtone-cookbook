(ns overtone-cookbook.core
  (:require [quil.core :as q]))

;; For whatever reason, we need to create a first sketch before
;; starting overtone, otherwise an exception is thrown from time to
;; time
(q/defsketch init
  :size [10 10]
  :draw #(q/exit))
(use 'overtone.live)

(comment
  ;; Sandbox
)
