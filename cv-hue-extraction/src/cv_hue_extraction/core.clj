(ns cv-hue-extraction.core
  (:gen-class)
  (:require [clojure.tools.cli :refer [parse-opts]])
  (:import (org.opencv.highgui Highgui)
           (org.opencv.core Core)
           (org.opencv.core CvType)
           (org.opencv.core Mat)
           (org.opencv.core Scalar)
           (org.opencv.imgproc Imgproc)
           (java.util List)
           (java.util ArrayList)))

(defn print-usage
  [options-summary program-name]
  (println
    "Usage:" program-name "[Options] FILE [...]\n"
    "File: Input file path\n"
    (str "Options:\n" options-summary)))
(defn print-version
  [program-name ver]
  (println program-name "Ver." ver))

(defn hue-extraction
    "src-image: Mat BGR Image
     hue: integer 1~180
     hue-range: integer 1~
    "
    [src hue hue-range]
    (let [hsv-image (Mat.)
          ^List hsv-image-list (ArrayList.)
          cv-max 255
          s-image (Mat. (.size src) CvType/CV_8UC1 (Scalar/all 0))
          v-image (Mat. (.size src) CvType/CV_8UC1 (Scalar/all 0))
          result (Mat. (.size src) CvType/CV_8UC3)]
        (Imgproc/cvtColor src hsv-image Imgproc/COLOR_BGR2HSV)

        (Core/split hsv-image hsv-image-list)
        (def hue-image (.clone (first hsv-image-list)))
        (Imgproc/threshold hue-image hue-image (+ hue hue-range) cv-max Imgproc/THRESH_TOZERO_INV)
        (Imgproc/threshold hue-image hue-image (- hue hue-range) cv-max Imgproc/THRESH_BINARY)

        (Core/bitwise_or s-image (Mat. (.size s-image) (.type s-image) (Scalar/all cv-max)) s-image (.clone hue-image))
        (Core/bitwise_or v-image (Mat. (.size v-image) (.type v-image) (Scalar/all cv-max)) v-image (.clone hue-image))
        (def dst (Mat. (.size src) CvType/CV_8UC3))
        (Core/merge (ArrayList. [hue-image s-image v-image]) dst)

        ;(def dst-bgr (Mat. (.size src) CvType/CV_8UC3))
        ;(Imgproc/cvtColor dst dst-bgr Imgproc/COLOR_HSV2BGR)
        ;(Imgproc/cvtColor dst-bgr result Imgproc/COLOR_BGR2RGB)

        dst))
        ;result))

(def option-spec
  [["-v" "--version" "Show program version."]
   ["-h" "--help" "Show help."]
   ["-o" "--out FILE" "name of the output file."
    :default "./dst.jpg"]
   ["-t" "--target TARGET" "Hue value of the target (required)."
    :parse-fn #(Integer/parseInt %)
    :validate [#(< 0 % 180) "Must be a number between 0 and 180"]]
   ["-r" "--range RANGE" "The range of values of the Hue of the target (r-t < t < r+t)."
    :default 10
    :parse-fn #(Integer/parseInt %)
    :validate [#(< 0 % 180) "Must be a number between 0 and 180"]]])

(defn -main
  [& args]
  (let [{:keys [options arguments errors summary]} (parse-opts args option-spec)]
    (println options arguments)
    (cond
      (:help options) (do (print-usage summary "senri") (System/exit 0))
      (:version options) (do (print-version "senri" "0.0.1") (System/exit 0))
      errors (do (println errors) (System/exit 1))
      (nil? (:target options)) (do (println "target is required.") (System/exit 1))
      (= (count arguments) 0) (do (println "FILE is required.") (System/exit 1)))

    (def image (Highgui/imread (first arguments)))
    (def processed (hue-extraction image (:target options) (:range options)))
    (Highgui/imwrite (:out options) processed)))
