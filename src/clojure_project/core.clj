(ns clojure-project.core)

; Utils
(defn get-expr-type [expr]
  (first expr))

(defn legal-expr? [expr expr-type]
  (= expr-type (get-expr-type expr)))

(defn expr-value [expr expr-type]
  (if (legal-expr? expr expr-type)
    (second expr)
    (throw (IllegalArgumentException. "Bad type"))))

(defn args_old [expr]
  (rest expr))

(defn args [expr]
  (rest expr))

; Primitives

(defn nm [value]
  (list ::name value))

(defn s [value]
  (list ::string value))

(defn tag [name & values]
  (concat (list ::tag name) values))

(defn arr [ & values]
  (cons ::arr values))


(defmulti to-str (fn [expr] (get-expr-type expr)))
(defmethod to-str ::string [expr] (expr-value expr ::string))
(defmethod to-str ::name [expr] (str (expr-value expr ::name)))
(defmethod to-str ::tag [expr] (reduce (fn [acc val] (str acc " " (to-str val))) "" (args expr)))

(defn expr-to-str [expr ]
  (to-str expr))

; Expression example
(tag (nm :note)
     (tag (nm :to) (s "Tove "))
     (tag (nm :from) (s "Jani") )
     (tag (nm :heading) (s "Reminder"))
     (tag (nm :body) (s "Don't forget me this weekend!") ))

; To str example
(to-str (tag (nm :note)
             (tag (nm :to) (s "Tove "))
             (tag (nm :from) (s "Jani") )
             (tag (nm :heading) (s  "Reminder"))
             (tag (nm :body) (s "Don't forget me this weekend!") )))


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




