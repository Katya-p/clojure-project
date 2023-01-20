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

(defmulti to-str (fn [expr] (get-expr-type expr)))
(defmethod to-str ::value [expr] (str expr))
(defmethod to-str ::name [expr] (str (expr-value expr ::name)))
(defmethod to-str ::tag [expr] (reduce (fn [acc val] (str acc " " (to-str val))) "" (args expr)))

; Expression example
(tag (nm :note)
     (tag (nm :to) "Tove ")
     (tag (nm :from) "Jani")
     (tag (nm :heading) "Reminder")
     (tag (nm :body) "Don't forget me this weekend!"))

; To str example
(to-str (tag (nm :note)
             (tag (nm :to) "Tove ")
             (tag (nm :from) "Jani")
             (tag (nm :heading) "Reminder")
             (tag (nm :body) "Don't forget me this weekend!")))

(defn path [& values]
   values)

(defn tag-content [expr]
  (if (legal-expr? expr ::tag)
    (rest (rest expr))
    (throw (IllegalArgumentException. "Bad type"))))

(defn tag-name [expr]
  (if (legal-expr? expr ::tag)
    (expr-value (first (filter #(legal-expr? % ::name) expr)) ::name)
    ""))

(defn apply-path [expr path]
  (if (legal-expr? expr ::tag)
    (reduce
      (fn [acc name]
        (reduce (fn [acc val] (concat acc (tag-content val)))
          (list)
          (filter (fn [elem] (= name (tag-name elem))) acc)))
      (list expr)
      path)
    (throw (IllegalArgumentException. "Bad expression"))))

(apply-path
  (tag (nm :html) (tag (nm :body)
                  (tag (nm :div) "First layer" (tag (nm :span) "Text in first layer"))
                  (tag (nm :div) "Second layer")
                  (tag (nm :div) "Third layer"
                       (tag (nm :span) "Text 1 in third layer")
                       (tag (nm :span) "Text 2 in third layer")
                       (tag (nm :span) "Text 3 in third layer"))
                  (tag (nm :div) "Fourth layer")))
  (path :html :body :div)
)

;ok
(path :html :body)
(path :html :body :div)
(path :html :body :div :span)
