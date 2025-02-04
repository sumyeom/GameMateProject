#!/bin/bash

# ECR 설정
AWS_ACCOUNT_ID="863518453426"
REGION="ap-northeast-2"
ECR_REPOSITORY="game_mate"
IMAGE_TAG="latest"
ECR_URI="$AWS_ACCOUNT_ID.dkr.ecr.$REGION.amazonaws.com/$ECR_REPOSITORY"

# AWS ECR 로그인
aws ecr get-login-password --region $REGION | docker login --username AWS --password-stdin $ECR_URI

# 최신 이미지 Pull
docker pull $ECR_URI:$IMAGE_TAG

# 기존 컨테이너 중지 및 삭제
docker stop app || true
docker rm app || true

# docker-compose 실행 (환경변수 적용)
docker-compose -f /home/ubuntu/docker-compose.yml up -d
