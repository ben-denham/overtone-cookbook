deps:
	lein deps
gen-site:
	lein exec scripts/gen-site.clj
deploy:
	git subtree push --prefix site origin gh-pages
