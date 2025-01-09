# invincible

A Clojure toy implementation of Oblivious Transfer protocol as stated by [Tung Chou and Claudio Orlandi](https://eprint.iacr.org/2015/267.pdf).

## Installing

Don't.

## Using

```clojure
;; 1. first step is setup:
(def prime (BigInteger. "prime-goes-here"))
(def generator (BigInteger. "generator-goes-here"))
(def setup-result (setup prime generator)) ;; returns :y (private key), :S (public key), and :T (derived key)

;; sender sends :S to the receiver

;; 2. next, the receiver runs `choose`, blinding its choice `c`:
(def choice 5) ;; Or any other value in [1, n].
(def choose-result (choose prime generator S choice)) ;; returns :Ri (public key) and :x (private key)

;; receiver sends :Ri to the sender

;; (now both sender and receiver can generate their keys using `sha3-256` as the default hash function)

;; 3. then, the sender generates `n` keys using `Ri` and private values (`y` and `T`):
(def sender-keys-result (sender-keys n prime Ri y T))

;; 4. finally, the receiver calculates its own key:
(def receiver-key-result (receiver-key prime S x))

;; that's it: the receiver can derive one key while remaining oblivious to the others
```
