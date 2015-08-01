(ns test.rek.board
  (:require [cljs.test :refer-macros [deftest testing is run-tests run-all-tests]]
            [cljs.test.check.cljs-test :refer-macros [defspec]]
            [cljs.test.check :as tc]
            [cljs.test.check.generators :as gen]
            [cljs.test.check.properties :as prop :include-macros true]))

(defn make-board-row [x [n pawn] & pawns]
  (let [make-pawn-positions (fn [x start end p]
                              (->>
                                (range start end)
                                (map (fn [y] [[x y] p]))))
        merge-position      (fn [coll [i p]]
                              (let [occupied-position (count coll)
                                    next-position     (+ i occupied-position)]
                                (conj coll
                                  (make-pawn-positions x occupied-position next-position p))))]
    (->>
      (reduce merge-position
        (make-pawn-positions x 0 n pawn) pawns)
      (into {}))))

;; = Rek Board
;; b = black pawn; B = black king pawn; w = white pawn; W = white king pawn; white pawn start first
;;   01234567
;; 0 bbbbbbb.
;; 1 .......B
;; 2 bbbbbbbb
;; 3 ........
;; 4 ........
;; 5 wwwwwwww
;; 6 W.......
;; 7 .wwwwwww
;; :s space
;; :b black pawn
;; :KB king balck pawn
;; :w white pawn
;; :KW king white pawn
(defn make-board []
  (merge
    (make-board-row 0 [7 :b] [1 :s])
    (make-board-row 1 [7 :s] [1 :KB])
    (make-board-row 2 [8 :b])
    (make-board-row 3 [8 :s])
    (make-board-row 4 [8 :s])
    (make-board-row 5 [8 :w])
    (make-board-row 6 [1 :KW] [7 :s])
    (make-board-row 7 [1 :s] [7 :w])))

(defn seq-pawn
  ([board] (reduce
             (fn [coll r] (concat coll (seq-pawn board r)))
             '()
             (range 8)))
  ([board row] (->> (map vector (repeat row) (range 8))
                 (select-keys board)
                 (vals))))

(deftest new-rek-board
  (let [board (make-board)]
    (testing "should be the size of 8x8"
      (is (= (count board) (* 8 8))))

    (testing "should contain 30 pawns, 15 normal pawns of each color"
      (is (and
            (= 15 (count (filter #{:b} (seq-pawn board))))
            (= 15 (count (filter #{:w} (seq-pawn board)))))))

    (testing "should contain 2 kings, one king pawn of each color"
      (is (and
            (= 1 (count (filter #{:KB} (seq-pawn board))))
            (= 1 (count (filter #{:KW} (seq-pawn board)))))))

    (testing "should contain 32 white spaces"
      (is (= 32 (count (filter #{:s} (seq-pawn board))))))

    (testing "should put all normal black pawns in 1st and 3rd rows"
      (is (= 15 (count (filter #{:b}
                         (concat (seq-pawn board 0) (seq-pawn board 2)))))))

    (testing "should put all normal white pawns in 6th and 8th rows"
      (is (= 15 (count (filter #{:w}
                         (concat (seq-pawn board 5) (seq-pawn board 7)))))))

    (testing "should put king black pawn in 2nd row"
      (is (= 1 (count (filter #{:KB} (seq-pawn board 1))))))

    (testing "should put king white pawn in 7th row"
      (is (= 1 (count (filter #{:KW} (seq-pawn board 6))))))

    (testing "should put space last in 1st row"
      (is (= :s (last (seq-pawn board 0)))))

    (testing "should put king black pawn last in 2nd row"
      (is (= :KB (last (seq-pawn board 1)))))

    (testing "should put space first in 8th row"
      (is (= :s (first (seq-pawn board 7)))))

    (testing "should put king white pawn first in 7th row"
      (is (= :KW (first (seq-pawn board 6)))))))


;; move-up, move-down, move-left, move-right :: Board -> x -> y -> n -> Board | IllegalMove

#_(def sort-idempotent-prop
    (prop/for-all [v (gen/vector gen/int)]
      (= (sort v) (sort (sort v)))))

#_(tc/quick-check 100 sort-idempotent-prop)

#_(defspec first-element-is-min-after-sorting               ;; the name of the test
    100                                                     ;; the number of iterations for test.check to test
    (prop/for-all [v (gen/not-empty (gen/vector gen/int))]
      (= (apply min v)
        (first (sort v)))))

#_(run-tests)
#_(run-all-tests [])