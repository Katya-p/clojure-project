(ns clojure-project.primitives)

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
        (reduce (fn [acc val] (concat acc (tag-content val)))
                (list)
                (filter (fn [elem] (apply-node elem node)) acc)))
      (list expr)
      path)
    (throw (IllegalArgumentException. "Bad expression"))))


;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(def data
  (tag (nm :data)
       (tag (nm :artist)
            (tag (nm :name) "Death")
            (tag (nm :genre) "DeathMetal")
            (tag (nm :song) "Bite the Pain"))
       (tag (nm :artist)
            (tag (nm :name) "Kiss")
            (tag (nm :genre) "HardRock")
            (tag (nm :song) "Strutter"))
       (tag (nm :artist)
            (tag (nm :name) "Red Hot Chili Peppers")
            (tag (nm :genre) "FunkRock")
            (tag (nm :song) "Under the Bridge"))
       (tag (nm :pic) "PIC")
       )
  )

(def template
  (tag
    (nm :html)
    (tag (nm :body)
         (tag (nm :div) "First layer"
              (tag (nm :span) "Text in first layer"))
         (tag (nm :div) "Second layer")
         (tag (nm :li)
              (tag (nm :select) (path :data :artist)
                   (tag (nm :ul)
                        (tag (nm :valueof) (path :genre)))))
         (tag (nm :img)
              (tag (nm :valueof) (path :data :pic)))
         (tag (nm :div) "Fourth layer"))))


(defn apply-template-helper
  [data template]
  (if (= (get-expr-type template) ::value)
    (list template)
    (if (= (tag-name template) :valueof)
      (list (first (apply-path  data (first (tag-content template)))))
      (if (= (tag-name template) :select)
        (map first
             (map (fn [inner]
                    (apply-template-helper inner (second (tag-content template))))
                  (apply-path data (first (tag-content template)))))
        (list (apply tag
                     (cons (tag-name template)
                           (mapcat (partial apply-template-helper data)
                                   (tag-content template)))))))))

(defn apply-template
  [data template]
  (first (apply-template-helper data template)))
(println (tag-content data))
(println (apply-template data template))
(println (apply-path data (path :data :artist)))

