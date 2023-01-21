(ns clojure-project.primitives)

; Utils

(defn get-expr-type [expr]
  (if (seq? expr) (first expr) ::value))

(defn legal-expr? [expr expr-type]
  (= expr-type (get-expr-type expr)))

(defn expr-value [expr expr-type]
  (if (legal-expr? expr expr-type)
    (second expr)
    (throw (IllegalArgumentException. "Bad type"))))

(defn args [expr]
  (rest expr))

; Primitives
(defn nm [value]
  (list ::name value))

(defn tag [name & values]
  (concat (list ::tag name) values))