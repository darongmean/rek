(ns darong.rek.app
  (:require [reagent.core :as reagent :refer [atom]]))

#_(defn some-component []
  [:div
   [:h3 "I am a component!"]
   [:p.someclass
    "I have " [:strong "bold"]
    [:span {:style {:color "red"}} " and red"]
    " text."]])

#_(defn calling-component []
  [:div "Parent component"
   [some-component]])

#_(defn init []
  (reagent/render-component [calling-component]
                            (.getElementById js/document "container")))

(defn init [])

;; board {:a {:1 :space
;;            :2 :pawn/boat
;;            :3 :pawn/king}}
;; or [[[:1 :space] [:2 :pawn/boat] [:3 :pawn/king]]]
;; or [[:space :pawn/boat :pawn/king]]
;; or [[:a :1 :space]]
;; or FEN Notation: ppppppp1/7k/pppppppp/8/8/PPPPPPPP/K7/1PPPPPPP w 0 1
;; (move boards [a 1] [a 2]) -> newBoards
;; (place-pawn board [a 1] [a 2]) -> newBoard
;; (rek board) -> newBoard
;; (left-pawn board [a 1]) -> pawn on the left of specified coordinate or :space or :none
;; (right-pawn board [a 1]) -> ... on the right ...
;; (below-pawn board [a 1]) -> ...below ...
;; (upper-pawn board [a 2]) -> ...upper ...
;; (win? board) true when king is being rek or surrounded