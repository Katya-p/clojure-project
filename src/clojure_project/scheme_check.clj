(ns clojure-project.scheme-check
  (:import [java.io PushbackReader])
  (:require [clojure.java.io :as io]
            [clojure.edn :as edn]
            [clojure-project.core :refer :all]))



(declare is-value)

(defn check-correctness
  [expr]
  (if (and (seq? expr) (= 3 (count expr)))
    true
    false))

(defn is-value
  [data]
  (if (or (string? data) (number? data))
    true
    (check-correctness data)))


(defn read-forms
  [file]
  (let [rdr (-> file io/file io/reader PushbackReader.)
        sentinel (Object.)]
    (loop [forms []]
      (let [form (edn/read {:eof sentinel} rdr)]
        (if (= sentinel form)
          forms
          (recur (conj forms form)))))))

(defn read-sdata
  [path]
  (let [data-seq (try
                   (read-forms path)
                   (catch Exception e (list))
                   (finally))]
    (first data-seq)))

(defn -main []
  (let [path-to-file1 "test/test-data/data1.txt"]
    (println (read-sdata path-to-file1))))



(defn zip
  [a b]
  (map vector a b))

(defn validate
  [tree schema]
  (if (empty? schema)
    (empty? tree)
    (apply (first schema) [tree (rest schema)])))

(defn tagg
  [tree schema]
  (and (= (first tree) (first schema))
       (validate (rest tree) (second schema))))

(defn sequencee
  [tree schema]
  (every? true? (map
                  (partial apply validate)
                  (zip tree schema))))

(defn stringg
  [tree schema]
  (string? (first tree)))

(defn numberr
  [tree schema]
  (number? (first tree)))

(defn xmlsequence
  [tree schema]
  (every? true? (map
                  (fn [node]
                    (some true? (map
                                  (partial validate node)
                                  schema)))
                  tree)))

;Пример схемы
(def example-scheme
  [tagg "note"
   [sequencee
    [tagg "to" [stringg]]
    [tagg "from" [stringg]]
    [tagg "heading" [stringg]]
    [tagg "body" [stringg]]
   ]
  ])

;Пример с-выражения
(def example
  ["note"
   ["to" "Tove"]
   ["from" "Jani"]
   ["heading" "Reminder"]
   ["body" "Don't forget me this weekend!"]
  ])

(println (sequencee
           [
            ["to" "Tove"]
            ["from" 123]
            ["heading" "Reminder"]
           ]
           [
            [tagg "to" [stringg]]
            [tagg "from" [numberr]]
            [tagg "heading" [stringg]]
           ]))

(println (validate
           ["note"
            ["to" "Tove"]
            ["from" "Jani"]
            ["heading" "Reminder"]
           ]
           [tagg "note"
            [sequencee
             [tagg "to" [stringg]]
             [tagg "from" [stringg]]
             [tagg "heading" [stringg]]
            ]
           ]))

(println (validate
           ["note"
            ["to" "Tove"]
            ["to" "Tove"]
            ["heading" "Reminder"]
           ]
           [tagg "note"
            [xmlsequence
             [tagg "to" [stringg]]
             [tagg "from" [stringg]]
             [tagg "heading" [stringg]]
            ]
           ]))


(println (validate ["note" "heh"] [tagg "note" [stringg]]))