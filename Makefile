deps:
	lein deps
build:
	lein marg -d site -f index.html
deploy:
	git subtree push --prefix site origin gh-pages
