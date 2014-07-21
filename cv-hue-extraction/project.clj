(defproject cv-hue-extraction "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :repositories {"The Seasar Foundation Maven2 Repository" "http://maven.seasar.org/maven2"}
  :dependencies [[org.clojure/clojure "1.5.1"]
                 [org.clojure/tools.cli "0.3.1"]]
  :jvm-opts ["-Djava.library.path=/usr/local/share/OpenCV/java"]
  :resource-paths ["/usr/local/share/OpenCV/java/opencv-247.jar"]
  :injections [(clojure.lang.RT/loadLibrary org.opencv.core.Core/NATIVE_LIBRARY_NAME)]
  :main ^:skip-aot cv-hue-extraction.core
  :target-path "target/%s"
  :profiles {:uberjar {:aot :all}})
