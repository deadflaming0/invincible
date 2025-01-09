(ns invincible.core
  (:import (java.security MessageDigest)
           (java.security SecureRandom)))

(defn- secure-random-sample
  [p]
  (BigInteger. (.bitLength p) (SecureRandom.)))

(defn setup
  "Sets up keys with `p` (prime modulus) and `g` (generator).
  Returns `:y` (secret), `:S` (public key), and `:T` (derived key)."
  ([p g]
   (setup secure-random-sample p g))
  ([sample-fn p g]
   (let [y (sample-fn p)
         S (.modPow g y p)
         T (.modPow S y p)]
     {:y y
      :S S
      :T T})))

(defn choose
  "Blinds choice `c` to generate `:Ri` (public key) and `:x` (private key)."
  ([p g S c]
   (choose secure-random-sample p g S c))
  ([sample-fn p g S c]
   (let [x (sample-fn p)
         Ri (.mod (.multiply (.modPow S c p) (.modPow g x p)) p)]
     {:Ri Ri
      :x x})))

(defn- sha3-256
  [input]
  (let [sha3-256 (MessageDigest/getInstance "SHA3-256")]
    (.update sha3-256 (.toByteArray input))
    (.digest sha3-256)))

(defn sender-keys
  "Generates `n` keys for the receiver using `Ri`, `y`, and `T`.
  Returns keys as byte[]."
  ([n p Ri y T]
   (sender-keys sha3-256 n p Ri y T))
  ([h n p Ri y T]
   (map (fn [j]
          (let [Riy (.modPow Ri y p)
                Tj (.modPow T (BigInteger/valueOf j) p)]
            (h (.mod (.multiply Riy (.modInverse Tj p)) p))))
        (range 1 (inc n)))))

(defn receiver-key
  "Generates the receiver's key using `S` and `x`.
  Returns the key as byte[]."
  ([p S x]
   (receiver-key sha3-256 p S x))
  ([h p S x]
   (h (.modPow S x p))))
