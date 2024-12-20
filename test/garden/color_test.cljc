(ns garden.color-test
  (:refer-clojure :exclude [complement])
  (:require
   #?(:cljs [cljs.test :as t :refer-macros [is are deftest testing]]
      :clj  [clojure.test :as t :refer [is are deftest testing]])
   [garden.color :as color])
  #?(:clj
     (:import clojure.lang.ExceptionInfo)))

(def hex-black "#000000")
(def hex-red "#ff0000")
(def hex-green "#00ff00")
(def hex-blue "#0000ff")
(def hex-white "#ffffff")

(def hexa-opaque-black "#000000ff")
(def hexa-black "#00000080")
(def hexa-red "#ff000080")
(def hexa-green "#00ff0080")
(def hexa-blue "#0000ff80")
(def hexa-white "#ffffff80")


(def rgb-black (color/rgb 0 0 0))
(def rgb-red (color/rgb 255 0 0))
(def rgb-green (color/rgb 0 255 0))
(def rgb-blue (color/rgb 0 0 255))
(def rgb-white (color/rgb 255 255 255))
(def rgb-orange (color/rgb 255 133 27))

(def rgba-opaque-black (color/rgba 0 0 0 1))
(def rgba-black (color/rgba 0 0 0 0.5))
(def rgba-red (color/rgba 255 0 0 0.5))
(def rgba-green (color/rgba 0 255 0 0.5))
(def rgba-blue (color/rgba 0 0 255 0.5))
(def rgba-white (color/rgba 255 255 255 0.5))
(def rgba-orange (color/rgba 255 133 27 0.5))

(def hsl-black (color/hsl 0 0 0))
(def hsl-red (color/hsl 0 100 50))
(def hsl-green (color/hsl 120 100 50))
(def hsl-blue (color/hsl 240 100 50))
(def hsl-white (color/hsl 0 0 100))
(def hsl-orange (color/hsl 530/19 100 940/17))

(def hsla-opaque-black (color/hsla 0 0 0 1))
(def hsla-black (color/hsla 0 0 0 0.5))
(def hsla-red (color/hsla 0 100 50 0.5))
(def hsla-green (color/hsla 120 100 50 0.5))
(def hsla-blue (color/hsla 240 100 50 0.5))
(def hsla-white (color/hsla 0 0 100 0.5))
(def hsla-orange (color/hsla 530/19 100 940/17 0.5))

(deftest color-conversion-test
  (testing "hex->rgb"
    (are [x y] (= x y)
      (color/hex->rgb hex-black) rgb-black
      (color/hex->rgb hex-red) rgb-red
      (color/hex->rgb hex-green) rgb-green
      (color/hex->rgb hex-blue) rgb-blue
      (color/hex->rgb hex-white) rgb-white))

  (testing "rgb->hex"
    (are [x y] (= x y)
      (color/rgb->hex rgb-black) hex-black
      (color/rgb->hex rgb-red) hex-red
      (color/rgb->hex rgb-green) hex-green
      (color/rgb->hex rgb-blue) hex-blue))

  (testing "rgba->hex"
    (are [x y] (= x y)
      (color/rgba->hex rgba-opaque-black) hexa-opaque-black
      (color/rgba->hex rgba-black) hexa-black
      (color/rgba->hex rgba-red) hexa-red
      (color/rgba->hex rgba-green) hexa-green
      (color/rgba->hex rgba-blue) hexa-blue))

  (testing "hsl->rgb"
    (are [x y] (= x y)
      (color/hsl->rgb hsl-black) rgb-black
      (color/hsl->rgb hsl-red) rgb-red
      (color/hsl->rgb hsl-green) rgb-green
      (color/hsl->rgb hsl-blue) rgb-blue
      (color/hsl->rgb hsl-white) rgb-white))

  (testing "hsla->rgba"
    (are [x y] (= x y)
      (color/hsla->rgba hsla-opaque-black) rgba-opaque-black
      (color/hsla->rgba hsla-black) rgba-black
      (color/hsla->rgba hsla-red) rgba-red
      (color/hsla->rgba hsla-green) rgba-green
      (color/hsla->rgba hsla-blue) rgba-blue
      (color/hsla->rgba hsla-white) rgba-white))

  (testing "rgb->hsl"
    (are [x y] (= x y)
      (color/rgb->hsl rgb-black) hsl-black
      (color/rgb->hsl rgb-red) hsl-red
      (color/rgb->hsl rgb-green) hsl-green
      (color/rgb->hsl rgb-blue) hsl-blue
      (color/rgb->hsl rgb-white) hsl-white
      (color/rgb->hsl rgb-orange) hsl-orange))

  (testing "as-hex"
    (are [x y] (= x y)
      (color/as-hex rgba-black) hexa-black
      (color/as-hex rgba-red) hexa-red
      (color/as-hex rgba-green) hexa-green
      (color/as-hex rgba-blue) hexa-blue
      (color/as-hex rgba-white) hexa-white

      (color/as-hex hsla-black) hexa-black
      (color/as-hex hsla-red) hexa-red
      (color/as-hex hsla-green) hexa-green
      (color/as-hex hsla-blue) hexa-blue
      (color/as-hex hsla-white) hexa-white)))

(deftest color-math-test
  (testing "color+"
    (are [x y] (= x y)
      (color/color+ (color/rgb 0 0 0))
      (color/rgb 0 0 0)

      (color/color+ (color/rgb 0 0 0) 1)
      (color/rgb 1 1 1)

      (color/color+ (color/rgb 0 0 0) 256)
      (color/rgb 255 255 255)

      (color/color+ 20 (color/rgb 130 130 130))
      (color/rgb 150 150 150)))

  (testing "color-"
    (are [x y] (= x y)
      (color/color- (color/rgb 0 0 0))
      (color/rgb 0 0 0)

      (color/color- (color/rgb 255 255 255) 256)
      (color/rgb 0 0 0)

      (color/color- 20 (color/rgb 150 150 150))
      (color/rgb 0 0 0)

      (color/color- (color/rgb 150 150 150) 20)
      (color/rgb 130 130 130)

      (color/color- (color/rgb 150 150 150) 20 30)
      (color/rgb 100 100 100)))

  (testing "color*"
    (are [x y] (= x y)
      (color/color* (color/rgb 0 0 0))
      (color/rgb 0 0 0)

      (color/color* (color/rgb 0 0 0) 1)
      (color/rgb 0 0 0)

      (color/color* (color/rgb 1 1 1) 5)
      (color/rgb 5 5 5)

      (color/color* 5 (color/rgb 1 1 1) 5)
      (color/rgb 25 25 25)))

  (testing "color-div"
    (are [x y] (= x y)
      (color/color-div (color/rgb 0 0 0))
      (color/rgb 0 0 0)

      (color/color-div (color/rgb 0 0 0) 1)
      (color/rgb 0 0 0)

      (color/color-div (color/rgb 1 1 1) 5)
      (color/rgb (/ 1 5) (/ 1 5) (/ 1 5))

      (color/color-div 5 (color/rgb 1 1 1))
      (color/rgb 5 5 5))

    #?(:clj
       (is (thrown? ArithmeticException
                    (color/color-div (color/rgb 1 1 1) 0))))))

(deftest color-functions-test
  (testing "rotate-hue"
    (are [x y] (= x y)
      (-> hsl-black (color/rotate-hue    0) :hue)   0
      (-> hsl-black (color/rotate-hue  180) :hue) 180
      (-> hsl-black (color/rotate-hue  360) :hue)   0
      (-> hsl-black (color/rotate-hue -360) :hue)   0
      (-> hsl-black (color/rotate-hue -180) :hue) 180))

  (testing "saturate"
    (are [x y] (= x y)
      (-> hsl-black (color/saturate   0) :saturation)   0
      (-> hsl-black (color/saturate  50) :saturation)  50
      (-> hsl-black (color/saturate 100) :saturation) 100
      (-> hsl-black (color/saturate 200) :saturation) 100))

  (testing "desaturate"
    (are [x y] (= x y)
      (-> hsl-red (color/desaturate   0) :saturation) 100
      (-> hsl-red (color/desaturate  50) :saturation)  50
      (-> hsl-red (color/desaturate 100) :saturation)   0
      (-> hsl-red (color/desaturate 200) :saturation)   0))

  (testing "lighten"
    (are [x y] (= x y)
      (-> rgb-black (color/lighten   0) :lightness)   0
      (-> rgb-black (color/lighten  50) :lightness)  50
      (-> rgb-black (color/lighten 100) :lightness) 100
      (-> rgb-black (color/lighten 200) :lightness) 100))

  (testing "darken"
    (are [x y] (= x y)
      (-> rgb-white (color/darken   0) :lightness) 100
      (-> rgb-white (color/darken  50) :lightness)  50
      (-> rgb-white (color/darken 100) :lightness)   0
      (-> rgb-white (color/darken 200) :lightness)   0))

  (testing "transparentize"
    (are [x y] (= x y)
      (-> (color/hsla 180 50 50 0.50) (color/transparentize 0.10) :alpha) 0.40
      (-> (color/hsla 180 50 50 0.50) (color/transparentize 0.50) :alpha) 0.00
      (-> (color/hsla 180 50 50 1.00) (color/transparentize 0.50) :alpha) 0.50
      (-> (color/hsla 180 50 50 0.01) (color/transparentize 0.10) :alpha) 0.00))

  (testing "opacify"
    (are [x y] (= x y)
      (-> (color/hsla 180 50 50 0.50) (color/opacify 0.10) :alpha) 0.60
      (-> (color/hsla 180 50 50 0.50) (color/opacify 0.50) :alpha) 1.00
      (-> (color/hsla 180 50 50 1.00) (color/opacify 0.50) :alpha) 1.00
      (-> (color/hsla 180 50 50 0.00) (color/opacify 0.12) :alpha) 0.12))

  (testing "invert"
    (are [x y] (= x y)
      (color/invert rgb-white)
      rgb-black

      (color/invert rgb-black)
      rgb-white)))

(deftest color-from-name-test
  (testing "from-name"
    (is (identical? (color/from-name "aquamarine")
                    (color/from-name "aquamarine")))
    (is (thrown? ExceptionInfo (color/from-name "aqualung")))))

(deftest scale-lightness-test []
  (testing "scale-lightness"
    (is (= 75 (-> (color/hsl 50 50 50) (color/scale-lightness   50) :lightness)))
    (is (= 25 (-> (color/hsl 50 50 50) (color/scale-lightness  -50) :lightness)))
    (is (= 20 (-> (color/hsl 50 50 10) (color/scale-lightness  100) :lightness)))
    (is (=  0 (-> (color/hsl 50 50 10) (color/scale-lightness -100) :lightness)))
    (is (= 15 (-> (color/hsl 50 50 10) (color/scale-lightness   50) :lightness)))
    (is (=  5 (-> (color/hsl 50 50 10) (color/scale-lightness  -50) :lightness)))))

(deftest scale-saturation-test []
  (testing "scale-lightness"
    (is (= 75 (-> (color/hsl 50 50 50) (color/scale-saturation   50) :saturation)))
    (is (= 25 (-> (color/hsl 50 50 50) (color/scale-saturation  -50) :saturation)))
    (is (= 20 (-> (color/hsl 50 10 50) (color/scale-saturation  100) :saturation)))
    (is (=  0 (-> (color/hsl 50 10 50) (color/scale-saturation -100) :saturation)))
    (is (= 15 (-> (color/hsl 50 10 50) (color/scale-saturation   50) :saturation)))
    (is (=  5 (-> (color/hsl 50 10 50) (color/scale-saturation  -50) :saturation)))))

(deftest scale-alpha-test []
  (testing "scale-alpha"
    (is (= 0.75 (-> (color/hsla 180 50 50 0.50) (color/scale-alpha  50) :alpha)))
    (is (= 0.25 (-> (color/hsla 180 50 50 0.50) (color/scale-alpha -50) :alpha)))))

(deftest hex-tests []
  (testing "decrown hex"
    (is (= "aabbcc" (#'garden.color/decrown-hex "#aabbcc"))))
  (testing "expand-hex"
    (is (= "aabbcc" (#'garden.color/expand-hex "#abc")))))

(deftest weighted-mix-test []
  (testing "weighted-mix basics"
    (is (= "#000000" (color/weighted-mix "#000" "#fff" 0)))))

(deftest tetrad-test []
  (testing "tetrad basics"
    (let [[{h1 :hue s1 :saturation l1 :lightness}
           {h2 :hue s2 :saturation l2 :lightness}
           {h3 :hue s3 :saturation l3 :lightness}
           {h4 :hue s4 :saturation l4 :lightness}]
          (color/tetrad (color/from-name "aquamarine"))]
      (is (= h1 5115/32))  (is (= s1 100N)) (is (= l1 3820/51))
      (is (= h2 10875/32)) (is (= s2 100N)) (is (= l2 3820/51))
      (is (= h3 7995/32))  (is (= s3 100N)) (is (= l3 3820/51))
      (is (= h4 2235/32))  (is (= s4 100N)) (is (= l4 3820/51)))))

(deftest shades-test []
  (testing "shades basics"
    (let [aquamarine-shades (color/shades (color/from-name "aquamarine"))]
      (is (every? (comp #{5115/32} :hue) aquamarine-shades))
      (is #(= [10 20 30 40 50 60 70 80 90]
              (map :lightness aquamarine-shades))))))
