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
  (path :html :body 0 :span)
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

(def data
  (tag (nm :data)
       (tag (nm :artist) (tag (nm :name) "Death") (tag (nm :genre) "DeathMetal") (tag (nm :song) "Bite the Pain"))
       (tag (nm :artist) (tag (nm :name) "Kiss") (tag (nm :genre) "HardRock") (tag (nm :song) "Strutter"))
       (tag (nm :artist) (tag (nm :name) "Red Hot Chili Peppers") (tag (nm :genre) "FunkRock") (tag (nm :song) "Under the Bridge"))
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
         (tag (nm :ul)
              (tag (nm :select) (path :data :artist) (tag (nm :li) (tag (nm :valueof) (path :artist :genre))))
              )
         (tag (nm :img) (tag (nm :valueof) (path :data :pic)))
         (tag (nm :div) "Fourth layer")
         )))

;(println (tag-content data))
;(println (apply-template data template))
;(println (apply-path data (path :data :artist)))


;(println (tag-content data))
;(println data)
;(println (apply-template data template))
;(println (apply-path-wrapper data (path :data :artist)))

;(println (tag-name data))
;(println (to-xml (apply-template data template)))