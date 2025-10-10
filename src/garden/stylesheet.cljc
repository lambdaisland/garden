(ns garden.stylesheet
  "Utility functions for CSS properties, directives and functions."
  (:require [garden.util :as util]
            [garden.color :as color]
            [garden.types :as t])
  #?(:clj
      (:import garden.types.CSSFunction
               garden.types.CSSAtRule)))

;;;; ## Stylesheet helpers

(defn rule
  "Create a rule function for the given selector. The `selector`
  argument must be valid selector (ie. a keyword, string, or symbol).
  Additional arguments may consist of extra selectors or
  declarations.

  The returned function accepts any number of arguments which represent
  the rule's children.

  Ex.
      (let [text-field (rule \"[type=\"text\"])]
       (text-field {:border [\"1px\" :solid \"black\"]}))
      ;; => [\"[type=\"text\"] {:border [\"1px\" :solid \"black\"]}]"
  [selector & more]
  (if-not (or (keyword? selector)
              (string? selector)
              (symbol? selector))
    (throw (ex-info
            "Selector must be either a keyword, string, or symbol." {}))
    (fn [& children]
      (into (apply vector selector more) children))))

(defn cssfn [fn-name]
  (fn [& args]
    (t/CSSFunction. fn-name args)))

;;;; ## At-rules

(defn- at-rule [identifier value]
  (t/CSSAtRule. identifier value))

(defn at-font-face
  "Create a CSS @font-face rule."
  [& font-properties]
  ["@font-face" font-properties])

(defn at-import
  "Create a CSS @import rule."
  ([url]
   (at-rule :import {:url url
                     :media-queries nil}))
  ([url & media-queries]
   (at-rule :import {:url url
                     :media-queries media-queries})))

(defn at-media
  "Create a CSS @media rule."
  [media-queries & rules]
  (at-rule :media {:media-queries media-queries
                   :rules rules}))

(defn at-supports
  "Create a CSS @supports rule."
  [feature-queries & rules]
  (at-rule :feature {:feature-queries feature-queries
                     :rules rules}))

(defn at-keyframes
  "Create a CSS @keyframes rule."
  [identifier & frames]
  (at-rule :keyframes {:identifier identifier
                       :frames frames}))

(defn at-page
  "Create a CSS @page rule."
  [named-page page-properties & rules]
  (at-rule :page {:named-page named-page
                  :page-properties page-properties
                  :rules rules}))


(defn at-container
  "Create a CSS @container rule. Supports both unnamed and named containers.
  
  Examples:
    (at-container {:min-width \"700px\"} [:h1 {:font-size \"2em\"}])
    (at-container :sidebar {:min-width \"700px\"} [:h1 {:font-size \"2em\"}])"
  [first-arg second-arg & rules]
  (if (map? first-arg)
    ;; First arg is container-queries (unnamed container)
    (at-rule :container {:container-name nil
                         :container-queries first-arg
                         :rules (cons second-arg rules)})
    ;; First arg is container-name (named container)
    (at-rule :container {:container-name first-arg
                         :container-queries second-arg
                         :rules rules})))


;; ## Functions

(defn rgb
  "Create a color from RGB values."
  [r g b]
  (color/rgb [r g b]))

(defn hsl
  "Create a color from HSL values."
  [h s l]
  (color/hsl [h s l]))
