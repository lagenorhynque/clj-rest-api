(disable-warning
 {:linter :constant-test
  :if-inside-macroexpansion-of #{'clojure.spec.alpha/every}
  :reason "clojure.spec.alpha/every can expand to code with constant test."})
