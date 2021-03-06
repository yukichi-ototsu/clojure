(ns opencv.core
    (:gen-class)
    (:import (org.seasar.framework.util StringUtil)
             (org.opencv.core Core)
             (org.opencv.core Mat)
             (org.opencv.imgproc Imgproc)
             (org.opencv.highgui Highgui)
             (java.util List)
             (java.util ArrayList)))

(defn hue-extraction
    [low_hue up_hue low_saturation low_value max_hue src]
    (let [hsv (Mat.)
          ^List single (doto (ArrayList.) (.add (Mat.)))
          hue (Mat.)
          hue1 (Mat.)
          hue2 (Mat.)
          saturation (Mat.)
          hue_saturation (Mat.)
          value (Mat.)
          result (Mat.)]
        (Imgproc/cvtColor src hsv Imgproc/COLOR_BGR2HSV)
        (Core/split hsv single)
        (Imgproc/threshold (.get single 0) hue1 low_hue max_hue Imgproc/THRESH_BINARY)
        (Imgproc/threshold (.get single 0) hue2 up_hue max_hue Imgproc/THRESH_BINARY_INV)
        (Imgproc/threshold (.get single 1) saturation low_saturation max_hue Imgproc/THRESH_BINARY)
        (Imgproc/threshold (.get single 2) value low_saturation max_hue Imgproc/THRESH_BINARY)
        (Core/bitwise_and hue1 hue2 hue)
        (Core/bitwise_and hue saturation hue_saturation)
        (Core/bitwise_and hue_saturation value result)
        result))

(defn -main
    [& args]
    (def low_hue 40)
    (def up_hue 80)
    (def low_saturation 60)
    (def low_value 80)
    (def max_hue 255)
    (def path "./")
    (def in-file (str path "src.png"))
    (def out-file (str path "dst.png"))
    (def image (Highgui/imread in-file))
    (def out-image (hue-extraction low_hue up_hue low_saturation low_value max_hue image))
    (Highgui/imwrite out-file out-image))
