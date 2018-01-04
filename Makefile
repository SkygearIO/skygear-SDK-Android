VERSION := v$(shell git describe --always)
DOCS_AWS_BUCKET := docs.skygear.io
DOCS_AWS_DISTRIBUTION := E31J8XF8IPV2V
DOCS_PREFIX = /android/reference

ifeq ($(VERSION),)
$(error VERSION is empty)
endif

.PHONY: build
build:
	./gradlew :skygear:build

.PHONY: clean
clean:
	-rm skygear/build

.PHONY: release-commit
release-commit:
	./scripts/release-commit.sh

.PHONY: doc-upload
doc-upload:
	aws s3 sync skygear/build/docs/javadoc s3://$(DOCS_AWS_BUCKET)$(DOCS_PREFIX)/$(VERSION) --delete

.PHONY: doc-invalidate
doc-invalidate:
	aws cloudfront create-invalidation --distribution-id $(DOCS_AWS_DISTRIBUTION) --paths "$(DOCS_PREFIX)/$(VERSION)/*"

.PHONY: doc-deploy
doc-deploy: clean build doc-upload doc-invalidate
