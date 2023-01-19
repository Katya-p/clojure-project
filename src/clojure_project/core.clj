(ns clojure-project.core)

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

(defn tag? [expr]
  (= (first expr) ::tag))

(defn nm? [expr]
  (= (second expr) ::name))

(defn a [name value]
  (list ::attribute name value))

(defmulti to-str (fn [expr] (get-expr-type expr)))
(defmethod to-str ::value [expr] (str expr))
(defmethod to-str ::name [expr] (str (expr-value expr ::name)))
(defmethod to-str ::tag [expr] (reduce (fn [acc val] (str acc " " (to-str val))) "" (args expr)))
(defmethod to-str ::attribute [expr] (reduce (fn [acc val] (str acc " " (to-str val))) "" (args expr)))

; Expression example
(tag (nm :note)
     (tag (nm :to) (a (nm :id) "555") "Tove ")
     (tag (nm :from) "Jani")
     (tag (nm :heading) "Reminder")
     (tag (nm :body) "Don't forget me this weekend!"))

; To str example
(to-str (tag (nm :note)
             (tag (nm :to) (a (nm :id) "555") "Tove ")
             (tag (nm :from) "Jani")
             (tag (nm :heading) "Reminder")
             (tag (nm :body) "Don't forget me this weekend!")))

(defn path [& values]
   values)

(defn get-content [expr]
  (rest (rest expr)))

(defn tag-name [expr]
  (expr-value (first (filter #(legal-expr? % ::name) expr)) ::name))

(defn apply-path [expr path]
   (reduce
    (fn [acc name]
      (first  (map get-content
        (filter (fn [elem] (= name (tag-name elem))) acc))
      ))
    (list expr)
    path))

(tag (nm :html) (tag (nm :body)
                (tag (nm :div) "First layer" (tag (nm :span) "Text in first layer"))
                (tag (nm :div) "Second layer")
                (tag (nm :div) "Third layer"
                     (tag (nm :span) (a (nm :class) "text") "Text 1 in third layer")
                     (tag (nm :span) (a (nm :class) "text") "Text 2 in third layer")
                     (tag (nm :span) "Text 3 in third layer"))
                (tag (nm :div) "Fourth layer")))

;ok
(path :html :body)
;not ok
(path :html :body :div)





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




