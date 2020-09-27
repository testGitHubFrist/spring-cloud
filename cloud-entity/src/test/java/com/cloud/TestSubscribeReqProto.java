package com.cloud;

import com.cloud.entity.proto.SubscribeReqProto;
import com.google.common.collect.Lists;
import com.google.protobuf.InvalidProtocolBufferException;

import java.util.List;

public class TestSubscribeReqProto {


    private static byte[] encode(SubscribeReqProto.SubscribeReq req) {

        return req.toByteArray();
    }

    private static SubscribeReqProto.SubscribeReq decode(byte[] body) throws InvalidProtocolBufferException {

        return SubscribeReqProto.SubscribeReq.parseFrom(body);
    }

    private static SubscribeReqProto.SubscribeReq createSubscribeReq() {
        SubscribeReqProto.SubscribeReq.Builder builder = SubscribeReqProto.SubscribeReq.newBuilder();

        builder.setSubReqID(1);
        builder.setUserName("zhangsan");
        builder.setProductName("netty book");
        List<String> addressList = Lists.newArrayList();
        addressList.add("beijing");
        addressList.add("nanjing");
        builder.addAllAddress(addressList);
        return builder.build();
    }

    public static void main(String[] args) throws Exception {
        SubscribeReqProto.SubscribeReq req = createSubscribeReq();
        System.out.println("before encode ：" + req.toString());

        SubscribeReqProto.SubscribeReq req2 = decode(encode(req));
        System.out.println("after decode ：" + req2.toString());
    }
}
