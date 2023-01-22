(ns clojure-project.scheme-check
  (:require [clojure-project.primitives :refer :all]))

(defn zip
  "Функция берёт на вход несколько списков и создаёт из них список,
  такой, что первый элемент полученного списка содержит кортеж из первых элементов всех списков-аргументов."
  [a b]
  (map vector a b))

(defn validate
  "Функция проверки документа по схеме. Если схема не пустая,
  то применяется первая функция в схеме(tagg/sequencee/stringg/xmlsequence)"
  [tree schema]
  (if (empty? schema)
    (empty? tree)
    (apply
      (first schema)
      [tree (rest schema)])))

(defn tagg
  "Функция проверки корректности тега. В теге дерева:
   1) на первом месте должен быть тег;
   2) на втором месте должно быть имя тега, совпадающего с указанным в схеме;
   3) в конце указаны данные, которые также должны пройти проверку."
  [tree schema]
  (and
    (= (first tree) :clojure-project.primitives/tag)
    (= (second tree) (nm (first schema)))
    (validate (rest (rest tree)) (second schema))))

(defn sequencee
  "Функция проводит строгую проверку последовательности тегов. Учитывается порядок."
  [tree schema]
  (every? true? (map
                  (partial apply validate)
                  (zip tree schema))))

(defn stringg
  "Функция проверки данных по типу string"
  [tree schema]
  (string? (first tree)))

(defn numberr
  "Функция проверки данных по типу number"
  [tree schema]
  (number? (first tree)))

(defn xmlsequence
  "Функция проводит НЕстрогую проверку последовательности тегов. НЕ учитывается порядок."
  [tree schema]
  (every? true?
          (map
            (fn [node]
              (some true? (map
                            (partial validate node)
                            schema)))
            tree)))