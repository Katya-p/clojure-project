(ns clojure-project.core)

; Utils
(defn get-expr-type [expr]
  (if (seq? expr)
    (first expr)
    (if (keyword? expr)
      ::name
      ::value)))

(defn legal-expr? [expr expr-type]
  (= expr-type (get-expr-type expr)))

(defn expr-value [expr expr-type]
  (if (legal-expr? expr expr-type)
    (second expr)
    (throw (IllegalArgumentException. "Bad type"))))

(defn args [expr]
  (rest expr))

; Primitives
(defn tag [name & values]
  (concat (list ::tag name) values))

(defn a [name value]
  (list ::attribute name value))

(defmulti to-str (fn [expr] (get-expr-type expr)))
(defmethod to-str ::value [expr] (str expr))
(defmethod to-str ::name [expr] (str expr))
(defmethod to-str ::tag [expr] (reduce (fn [acc val] (str acc " " (to-str val))) "" (args expr)))
(defmethod to-str ::attribute [expr] (reduce (fn [acc val] (str acc " " (to-str val))) "" (args expr)))

; Expression example
(tag :note
     (tag :to (a :id "555") "Tove ")
     (tag :from "Jani")
     (tag :heading "Reminder")
     (tag :body "Don't forget me this weekend!"))

; To str example
(to-str (tag :note
             (tag :to (a :id "555") "Tove ")
             (tag :from "Jani")
             (tag :heading "Reminder")
             (tag :body "Don't forget me this weekend!")))


; Dnf
(defn conjj [expr & rest]
  (cons ::and (cons expr rest)))

(defn disjj [expr & rest]
  (cons ::or (cons expr rest)))

(defn neg [expr]
  (list ::not expr))

(defn impl [expr1 expr2]
  (list ::impl expr1 expr2))

(defmulti evaluate (fn [expr _] (get-expr-type expr)))
(defmethod evaluate ::const [expr _] (expr-value expr ::const))
(defmethod evaluate ::var [expr values] (get values (expr-value expr ::var)))
(defmethod evaluate ::not [expr values] (not (evaluate (first (args expr)) values)))
(defmethod evaluate ::and [expr values] (reduce (fn [acc val] (and acc (evaluate val values))) true (args expr)))
(defmethod evaluate ::or [expr values] (reduce (fn [acc val] (or acc (evaluate val values))) false (args expr)))

(defn evaluate-dnf [expr values]
  (evaluate expr values))




