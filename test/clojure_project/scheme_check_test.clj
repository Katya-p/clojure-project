(ns clojure-project.scheme-check-test
  (:require [clojure.test :refer :all]
            [clojure-project.primitives :refer :all]
            [clojure-project.scheme-check :refer :all]))


(def tree1
  (tag (nm :html)
       (tag (nm :body)
            (tag (nm :div) "First layer"
                 (tag (nm :span) "Text in first layer"))
            (tag (nm :div) "Second layer")
            (tag (nm :div) "Third layer"
                 (tag (nm :span) "Text 1 in third layer")
                 (tag (nm :span) "Text 2 in third layer")
                 (tag (nm :span) "Text 3 in third layer"))
            (tag (nm :div) "Fourth layer"))))

(def schema1
  [tagg :html
   [xmlsequence
    [tagg :body
     [xmlsequence
      [tagg :div [stringg]]
      [tagg :div
       [xmlsequence
        [tagg :span [stringg]]]]]]]])

(def tree2
  (tag (nm :hhhh)
       (tag (nm :body)
            (tag (nm :div) "First layer"
                 (tag (nm :span) "Text in first layer"))
            (tag (nm :div) "Second layer")
            (tag (nm :div) "Third layer"
                 (tag (nm :span) "Text 1 in third layer")
                 (tag (nm :span) "Text 2 in third layer")
                 (tag (nm :span) "Text 3 in third layer"))
            (tag (nm :div) "Fourth layer"))))

(def schema2
  [tagg :html
   [xmlsequence
    [tagg :body
     [xmlsequence
      [tagg :div [stringg]]
      [tagg :div
       [xmlsequence
        [tagg :span [stringg]]]]]]]])

(def tree3
  (tag (nm :note)
       (tag (nm :to) "Tove")
       (tag (nm :from) "Jani")
       (tag (nm :heading) "Reminder")
       (tag (nm :body) "Don't forget me this weekend!")))

(def schema3
  [tagg :note
   [sequencee
    [tagg :to [stringg]]
    [tagg :from [stringg]]
    [tagg :heading [stringg]]
    [tagg :body [stringg]]]])

(def tree4
  (tag (nm :note)
       (tag (nm :body) "Don't forget me this weekend!")
       (tag (nm :heading) "Reminder")
       (tag (nm :from) "Jani")
       (tag (nm :to) "Tove")))

(def schema4
  [tagg :note
   [xmlsequence
    [tagg :to [stringg]]
    [tagg :from [stringg]]
    [tagg :heading [stringg]]
    [tagg :body [stringg]]]])

(deftest validate-test-empty
  (testing "Тест если схема и дерево пустые")
  (is (= (validate () ()) true)))

(deftest validate-test1
  (testing "Тест если дерево построено по схеме")
  (is (= (validate tree1 schema1) true)))

(deftest validate-test2
  (testing "Тест если в дереве неправильное имя")
  (is (= (validate tree2 schema2) false)))

(deftest validate-test3
  (testing "Тест если дерево построено по схеме c sequencee")
  (is (= (validate tree3 schema3) true)))

(deftest validate-test31
  (testing "Тест если дерево построено по схеме c xmlsequence")
  (is (= (validate tree3 schema4) true)))

(deftest validate-test32
  (testing "Тест если дерево построено по схеме c sequencee но с порядком для xmlsequence")
  (is (= (validate tree4 schema3) false)))

(deftest validate-test4
  (testing "Тест если дерево построено по схеме c xmlsequence")
  (is (= (validate tree4 schema4) true)))

