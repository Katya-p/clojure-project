(ns clojure-project.primitives)
(use '[clojure.string :only (replace-first)])

; Utils
(defn get-expr-type
  [expr]
  (if (seq? expr) (first expr) ::value))

(defn legal-expr?
  [expr expr-type]
  (= expr-type (get-expr-type expr)))

(defn expr-value
  [expr expr-type]
  (if (legal-expr? expr expr-type)
    (second expr)
    (throw (IllegalArgumentException. "Bad type"))))

(defn args
  [expr]
  (rest expr))

; Primitives
(defn nm
  [value]
  (list ::name value))

(defn tag
  [name & values]
  (concat (list ::tag name) values))

(defn tag-content
  [expr]
  (if (legal-expr? expr ::tag)
    (rest (rest expr))
    (throw (IllegalArgumentException. "Bad type"))))

(defn tag-name
  [expr]
  (if (legal-expr? expr ::tag)
    (expr-value (first (filter #(legal-expr? % ::name) expr)) ::name)
    ""))

; Document to string
(defmulti to-str (fn [expr] (get-expr-type expr)))
(defmethod to-str ::value [expr] (str expr))
(defmethod to-str ::name [expr] (str (expr-value expr ::name)))
(defmethod to-str ::tag [expr] (reduce (fn [acc val] (str acc " " (to-str val))) "" (args expr)))

; Path
(defn path
  [& values]
  values)

; Check if expression satisfies node
; The node is the element of the path
(defmulti apply-node (fn [expr node] node))
(defmethod apply-node :* [expr node] (legal-expr? expr ::tag))
(defmethod apply-node :default  [expr node] (= node (tag-name expr)))

; Get the content of the document that satisfies path
(defn apply-path
  [expr path]
  (if (legal-expr? expr ::tag)
    (reduce
      (fn [acc node]
        (mapcat tag-content (filter (fn [elem] (apply-node elem node)) acc)))
      ;    (reduce (fn [acc val] (concat acc (tag-content val)))
      ;      (list)
      ;      (filter (fn [elem] (apply-node elem node)) acc)))
      (list expr)
      path)
    (throw (IllegalArgumentException. "Bad expression"))))


;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(defn apply-path-wrapper
  [expr path]
  (filter (fn [elem] (apply-node elem (last path)))
          (apply-path expr (drop-last path))))

(defn apply-template-helper
  [data template]
  (if (= (get-expr-type template) ::value)
    (list template)
    (if (= (tag-name template) :valueof)
      (list (first (apply-path data (first (tag-content template)))))
      (if (= (tag-name template) :select)
        (map first
             (map (fn [inner]
                    (apply-template-helper inner (second (tag-content template))))
                  (apply-path-wrapper data (first (tag-content template)))))
        (list (apply tag
                     (cons (nm (tag-name template))
                           (mapcat (partial apply-template-helper data)
                                   (tag-content template)))))))))

(defn apply-template
  [data template]
  (first (apply-template-helper data template)))

(defn trim-tag-name [expr] (replace-first (tag-name expr) #":" ""))

(defmulti to-xml (fn [expr] (get-expr-type expr)))
(defmethod to-xml ::value [expr] (str expr))
(defmethod to-xml ::tag [expr] (str "<" (trim-tag-name expr) ">" (apply str (map to-xml (tag-content expr))) "</" (trim-tag-name expr) ">"))


