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

(defn node-type [node]
  (if (number? node) :idx node))

(defmulti pred (fn [expr node] (node-type node)))
(defmethod pred :* [expr node] (legal-expr? expr ::tag))
(defmethod pred :idx [expr node] (throw (IllegalArgumentException. "Bad type")))
(defmethod pred :default  [expr node] (= node (tag-name expr)))

(defn get-node-content [expr-list node]
  (if (= (node-type node) :idx)
    (throw (IllegalArgumentException. "Bad type"))
    (reduce (fn [acc val] (concat acc (tag-content val)))
            (list)
            (filter (fn [elem] (pred elem node)) expr-list))))

(defmulti apply-node (fn [expr node] (node-type node)))
(defmethod apply-node :* [expr node] (get-node-content expr node))
(defmethod apply-node :idx [expr node] (nth expr node))
(defmethod apply-node :default  [expr node] (get-node-content expr node))

(defn apply-path
  "??????? ?????????? ?????????? ?????????, ??????????????? ????"
  [expr path]
  (if (legal-expr? expr ::tag)
    (reduce
      (fn [acc node]
        (apply-node acc node))
      (list expr)
      path)
    (throw (IllegalArgumentException. "Bad expression"))))

(defn add-string
  "???????? ?????? ? ??????"
  [s expr]
  (if (not (legal-expr? expr ::value))
    expr
    (str expr s)))

(defn add-string-tag
  "???????? ?????? ? ??????????? ????, ???? ??? ???????? ???????"
  [s expr]
  (if (not (legal-expr? expr ::tag))
    expr
    (tag (tag-name expr)
         (map
           (fn [val] (if (legal-expr? val ::value)
                       (str val s) val))
           (tag-content expr)))))

(defn modify-doc
  "??????? ?????????? ?????????? ?????????, ??????????????? ???? ? ?????????? ? ??????? func"
  [expr path func func-arg]
  (if (not (legal-expr? expr ::tag))
    (throw (IllegalArgumentException. "Bad expression"))
    (map (partial func func-arg) (apply-path expr path))
   ))
   
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

