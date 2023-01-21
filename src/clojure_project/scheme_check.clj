(ns clojure-project.scheme-check
  (:require [clojure-project.primitives :refer :all]))

(defn zip
  [a b]
  (map vector a b))

(defn validate
  [tree schema]
  (if (empty? schema)
    (empty? tree)
    (apply
      (first schema)
      [tree (rest schema)])))

(defn tagg
  [tree schema]
  (and
    (= (first tree) :clojure-project.primitives/tag)
    (= (second tree) (nm (first schema)))
    (validate (rest (rest tree)) (second schema))))

(defn sequencee
  [tree schema]
  (every? true? (map
                  (partial apply validate)
                  (zip tree schema))))

(defn stringg
  [tree schema]
  (string? (first tree)))

(defn xmlsequence
  [tree schema]
  (every? true?
          (map
            (fn [node]
              (some true? (map
                            (partial validate node)
                            schema)))
            tree)))