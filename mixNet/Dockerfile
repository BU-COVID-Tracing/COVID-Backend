FROM golang:alpine

ENV GO111MODULE=auto \
    CGO_ENABLED=0 \
    GOOS=linux \
    GOARCH=amd64

WORKDIR /go/src/app
COPY . .
RUN apk update && apk add git
RUN go get -u github.com/gorilla/mux
EXPOSE 8081
ENTRYPOINT ["go","run","MixNet.go"]

# ENTRYPOINT ["MixNet"]
