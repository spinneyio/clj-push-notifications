(defproject clj-push-notifications "1.0.9"
  :description "Firebase cloud messaging client."
  :url "https://github.com/spinneyio/clj-push-notifications"
  :license {:name "EPL-2.0 OR GPL-2.0-or-later WITH Classpath-exception-2.0"
            :url "https://www.eclipse.org/legal/epl-2.0/"}
  :dependencies [[org.clojure/clojure "1.11.2"]
                 [com.google.firebase/firebase-admin "9.2.0" :exclusions
                  [com.google.errorprone/error_prone_annotations
                   io.grpc/grpc-api
                   io.grpc/grpc-core
                   io.grpc/grpc-netty-shaded]]])
