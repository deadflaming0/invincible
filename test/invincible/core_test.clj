(ns invincible.core-test
  (:require [clojure.set :as set]
            [clojure.test :refer :all]
            [invincible.core :as core]))

(defn- big-integer
  [x]
  (BigInteger/valueOf x))

(defn- ba->hs
  "Converts byte arra to hexadecimal string for testing purposes."
  [input]
  (apply str (map #(format "%02x" %) input)))

(deftest protocol-test
  (testing "toy example"
    (let [n 100
          p (big-integer 23)
          g (big-integer 5)
          y (big-integer 3)
          {:keys [S T]} (core/setup (constantly y) p g)
          x (big-integer 5)
          c (big-integer (inc (rand-int n)))
          {:keys [Ri x]} (core/choose (constantly x) p g S c)
          sender-keys (map ba->hs (core/sender-keys n p Ri y T))
          receiver-key (ba->hs (core/receiver-key p S x))]
      (is (= receiver-key (nth sender-keys (dec c))))
      (is (= 1 (count (set/intersection (set sender-keys) #{receiver-key})))))))
