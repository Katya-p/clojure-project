(ns clojure-project.core
  [:require [clojure-project.primitives :refer :all]
            [clojure-project.scheme-check :refer :all]])

; Expression example
(tag (nm :note)
     (tag (nm :to) "Tove ")
     (tag (nm :from) "Jani")
     (tag (nm :heading) "Reminder")
     (tag (nm :body) "Don't forget me this weekend!"))

;; To str example
(to-str (tag (nm :note)
             (tag (nm :to) "Tove ")
             (tag (nm :from) "Jani")
             (tag (nm :heading) "Reminder")
             (tag (nm :body) "Don't forget me this weekend!")))


(apply-path
  (tag (nm :html) (tag (nm :body)
                       (tag (nm :div) "First layer"
                            (tag (nm :span) "Text in first layer"))
                       (tag (nm :div) "Second layer")
                       (tag (nm :div) "Third layer"
                            (tag (nm :span) "Text 1 in third layer")
                            (tag (nm :span) "Text 2 in third layer")
                            (tag (nm :span) "Text 3 in third layer"))
                       (tag (nm :div) "Fourth layer")))
  (path :html :body :div :* 0)
  )

;ok
(path :html :body)
(path :html :body :div)
(path :html :body :div :span)

(def tree
  (tag (nm :html)
       (tag (nm :body)
            (tag (nm :div) "First layer"
                 (tag (nm :span) "Text in first layer"))
            (tag (nm :div) "Second layer")
            (tag (nm :div) "Third layer"
                 (tag (nm :span) "Text 1 in third layer")
                 (tag (nm :span) "Text 2 in third layer")
                 (tag (nm :span) "Text 3 in third layer"))
            (tag (nm :div) "Fourth layer")
            ))
  )

(def schema
  [tagg :html
   [xmlsequence
    [tagg :body
     [xmlsequence
      [tagg :div [stringg]]
      [tagg :div
       [xmlsequence
        [tagg :span [stringg]]]]]]]])


(println (validate tree schema))