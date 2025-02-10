#!/bin/bash

# ECR 설정
AWS_ACCOUNT_ID="863518453426"
REGION="ap-northeast-2"
ECR_REPOSITORY="game_mate"
IMAGE_TAG="latest"
ECR_URI="$AWS_ACCOUNT_ID.dkr.ecr.$REGION.amazonaws.com/$ECR_REPOSITORY"

# AWS ECR 로그인
aws ecr get-login-password --region $REGION | sudo docker login --username AWS --password-stdin $ECR_URI

# 기존 컨테이너 중지 및 삭제
if [ "$(sudo docker ps -q)" ]; then
    sudo docker-compose down
fi

# 기존 이미지 삭제 (최신 이미지 사용을 보장)
if [ "$(sudo docker images -q $ECR_URI:$IMAGE_TAG)" ]; then
    sudo docker rmi -f $ECR_URI:$IMAGE_TAG
fi

# 최신 이미지 Pull
sudo docker pull $ECR_URI:$IMAGE_TAG

# docker-compose 실행 경로 설정
cd /home/ubuntu

# docker-compose 실행
sudo docker-compose -f docker-compose.yml up -d --force-recreate
