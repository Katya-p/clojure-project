(ns clojure-project.scheme-check
  (:import [java.io PushbackReader])
  (:require [clojure.java.io :as io]
            [clojure.edn :as edn]
            [clojure-project.core :refer :all]))



(declare is-value)

(defn check-correctness [expr]
  (if (and (seq? expr) (= 3 (count expr)))
    true
    false))

(defn is-value
  [data]
  (if (or (string? data) (number? data))
    true
    (check-correctness data)))


(defn read-forms [file]
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
    (first (filter
             (fn [data]
               (check-correctness data))
             data-seq))))

(defn -main []
  (let [path-to-file1 "test/test-data/data1.txt"]
    (println (read-sdata path-to-file1))))



;Пример с-выражения
(def example (tag :note
                  '(
                    (tag :to "Tove")
                    (tag :from "Jani" )
                    (tag :heading "Reminder")
                    (tag :body "Don't forget me this weekend!")
                    )
                  ))

(def example (tag :note
                  [
                   (tag :to "Tove")
                   (tag :from "Jani" )
                   (tag :heading "Reminder")
                   (tag :body "Don't forget me this weekend!")
                   ]
                  ))

;Пример схемы
(def example-scheme '(::tag :note
                       (::sequence
                         (
                          (::tag :to ::string)
                          (::tag :from ::string )
                          (::tag :heading ::string)
                          (::tag :body ::string)
                          )
                         )
                       ))