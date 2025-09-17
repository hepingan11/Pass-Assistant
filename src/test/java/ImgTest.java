


public class ImgTest {





//        LocalDateTime localDateTime = LocalDateTime.now();
//        System.out.println(localDateTime);
//        String name =  LocalDateTime.now().toString().replace(":","-").replace(".","-");
//        System.out.println(name);
//        CloseableHttpClient aDefault = HttpClients.createDefault();
//
//        HttpPost httpPost =new HttpPost("https://sd.hepingan.top/sdapi/v1/txt2img");
//        JSONObject param = new JSONObject();
//        param.put("prompt", "girl");
//        param.put("step",10);
//        param.put("height",512);
//        param.put("width",512);
//        param.put("override_settings",new JSONObject().put("sd_model_checkpoint","majicMIX realistic_v6.safetensors"));
//
//        StringEntity stringEntity=new StringEntity(param.toString());
//        Header header=new BasicHeader("Content-Type","application/json");
//        httpPost.setHeader(header);
//        httpPost.setHeader("Authorization","Basic Og== ");
//        httpPost.setHeader("Connection","keep-alive");
//        httpPost.setEntity(stringEntity);
//        CloseableHttpResponse execute = aDefault.execute(httpPost);
//
////        System.out.println(execute);
//        String s = EntityUtils.toString(execute.getEntity());
//
//        System.out.println(s);
//        AliUploadUtils aliUploadUtils = new AliUploadUtils();
//
//        String imageUri = aliUploadUtils.uploadBase64(Objects.requireNonNull(JSONObject.parseObject(s)).getJSONArray("images").getString(0), FileEnum.PAINTING.getDec());
//
//        System.out.println(imageUri);

}
