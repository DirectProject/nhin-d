import java.io.IOException;

import javax.ws.rs.core.MediaType;

import org.apache.commons.io.IOUtils;
import org.apache.http.Header;
import org.apache.http.HeaderElement;
import org.apache.http.HttpEntity;
import org.apache.http.HttpException;
import org.apache.http.HttpResponse;
import org.apache.http.HttpResponseInterceptor;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.GzipDecompressingEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.HttpContext;
import org.nhindirect.common.rest.HttpClientFactory;


public class BOGScraper 
{
	public static void main(String[] args)
	{
		

			DefaultHttpClient client = (DefaultHttpClient)HttpClientFactory.createHttpClient();
			
			
			int day = 1;
			String time = "dinner";
			
			
			while(true)
			{
				try
				{
				
					client.addResponseInterceptor(new HttpResponseInterceptor() {
		
		                public void process(
		                        final HttpResponse response,
		                        final HttpContext context) throws HttpException, IOException {
		                    HttpEntity entity = response.getEntity();
		                    if (entity != null) {
		                        Header ceheader = entity.getContentEncoding();
		                        if (ceheader != null) {
		                            HeaderElement[] codecs = ceheader.getElements();
		                            for (int i = 0; i < codecs.length; i++) {
		                                if (codecs[i].getName().equalsIgnoreCase("gzip")) {
		                                    response.setEntity(
		                                            new GzipDecompressingEntity(response.getEntity()));
		                                    return;
		                                }
		                            }
		                        }
		                    }
		                }
		
		            });
					
					HttpPost post = new HttpPost("https://disneyworld.disney.go.com/activities/dining-availability");
					
					post.setHeader("Accept", "*/*");
					post.setHeader("Accept-Encoding", "gzip,deflate,sdch");
					post.setHeader("Accept-Language", "en-US,en;q=0.8");
					post.setHeader("Connection", "keep-alive");
					//post.setHeader("Content-Length", "178");
					post.setHeader("Content-Type", "application/x-www-form-urlencoded");
					post.setHeader("Cookie", "optimizelyEndUserId=oeu1358558437847r0.07084921281784773; __qca=P0-661515119-1358558438978; dolWA30=1362951732650; grvinsights=ee419b7b50b4f796e86088c3b1d2ad42; cto_firstUrl=\"http://disneyrewards.com/perks/disneyland-perks\"; cto_visitorId=1377887977144-9354867790880; cto_firstPageName=\"dcore:drv:perks:disneyland-perks\"; cto_sessionCount=1; cto_firstRefUrl=na; ctoTimeStamp=1387999595863; backplane-id=41231508470D4B238A7306575348FC41; bp_channel_id=https%3A%2F%2Fapi.echoenabled.com%2Fv1%2Fbus%2Fespn.go.com%2Fchannel%2F41231508470D4B238A7306575348FC41; fbm_116656161708917=base_domain=.go.com; rampenableCheckoutGuestPoc_A=%7B%22enableCheckoutGuestPoc%22%3Atrue%7D; analytics_targus=074%3B2978363885011DB0%7C6000010F800014AC; s_fid=52C9B90BC5607FB9-09F500455ED40B17; gi=usa|mo|kansas city|cable|39.009|-94.402|64118|f02fc4f4; rememberme=%7B%22name%22%3A%22Greg%22%2C%22lastName%22%3A%22Meyer%22%2C%22avatar%22%3A%2217532228%22%2C%22swid%22%3A%22%7BACA6E4C3-4397-43EF-A4AD-259926923E09%7D%22%2C%22passiveId%22%3A%2259f894315e2a19e7402e67fe4c6ff747da4e010fe12261eae24d14c73be8a8c57ccad22feb6201c1d8c4dde74475121d59bb112697d7f5753362f6c5eff25a68%22%2C%22email%22%3A%22kmeyer%40kc.rr.com%22%7D; SWID=ACA6E4C3-4397-43EF-A4AD-259926923E09; GEOLOCATION_jar=%7B%22region%22%3A%22missouri%22%2C%22country%22%3A%22united+states%22%7D; roomForm_jar=%7B%22checkInDate%22%3A%222014-09-01%22%2C%22checkOutDate%22%3A%222014-09-08%22%2C%22numberOfAdults%22%3A%222%22%2C%22numberOfChildren%22%3A%221%22%2C%22accessible%22%3A%220%22%2C%22resort%22%3A%2280010394%3BentityType%3Dresort%22%2C%22kid1%22%3A%2216%22%7D; currentOffer_jar=%7B%22currentOffer%22%3A%22resort-stay-dining%22%7D; 55170107-VID=53301077421770; optimizelySegments=%7B%22166049822%22%3A%22none%22%2C%22166646996%22%3A%22false%22%2C%22167330480%22%3A%22search%22%2C%22167351530%22%3A%22gc%22%2C%22173942781%22%3A%22gc%22%2C%22174819705%22%3A%22false%22%2C%22175220085%22%3A%22search%22%2C%22175226102%22%3A%22referral%22%2C%22175226103%22%3A%22none%22%2C%22175370703%22%3A%22false%22%2C%22175404369%22%3A%22none%22%2C%22175412291%22%3A%22gc%22%2C%22310954789%22%3A%22none%22%2C%22311043393%22%3A%22false%22%2C%22311047346%22%3A%22gc%22%2C%22311052307%22%3A%22referral%22%2C%22793783561%22%3A%22true%22%2C%22806111078%22%3A%22none%22%2C%22806950111%22%3A%22desktop%22%7D; optimizelyBuckets=%7B%7D; CRBLM=CBLM-001:AAAAAAABBGwAAAAB; CRBLM_LAST_UPDATE=1400616530:ACA6E4C3-4397-43EF-A4AD-259926923E09; PHPSESSID=oq7t0rqmdj9j4lb99b97r33vq3; CART-wdw_jar=%7B%22cartId%22%3A%22124935657330-9131021-6107974-2755370%22%7D; mbox=level#20#1403208529|PC#1391624422331-680398.17_31#1408406023|traffic#false#1407237700|check#true#1400630083|session#1400630022656-797538#1400631883; wdpro_seen_cmps=/55541/%2C/55542/%2C/56321/%2C/53949/; pep_oauth_token=BpZ7TgrYTjMmmNfOJcYmNA; boomr_rt=cl=1400630030984&nu=https%3A%2F%2Fdisneyworld.disney.go.com%2Fdining%2Fmagic-kingdom%2Fbe-our-guest-restaurant%2F%23; localeCookie_jar=%7B%22contentLocale%22%3A%22en_US%22%2C%22precedence%22%3A0%2C%22version%22%3A%221%22%7D; WDPROView=%7B%22version%22%3A2%2C%22preferred%22%3A%7B%22device%22%3A%22desktop%22%2C%22screenWidth%22%3A250%2C%22screenHeight%22%3A150%2C%22screenDensity%22%3A1%7D%2C%22deviceInfo%22%3A%7B%22device%22%3A%22desktop%22%2C%22screenWidth%22%3A250%2C%22screenHeight%22%3A150%2C%22screenDensity%22%3A1%7D%7D; s_vi=[CS]v1|2978363885011DB0-6000010F800014AC[CE]; s_pers=%20s_cpm%3D%255B%255B'SOC-DPFY14Q2EpcotInternationalFood'%252C'1392899303875'%255D%252C%255B'SOC-DPFY14Q2EpcotInternationalFood'%252C'1392899304121'%255D%252C%255B'SOC-DPFY14Q2EpcotInternationalFood'%252C'1392899304679'%255D%252C%255B'SOC-DPFY14Q2EpcotInternationalFood'%252C'1392899310315'%255D%252C%255B'SOC-DPFY14Q2EpcotInternationalFood'%252C'1392899314146'%255D%255D%7C1550665714146%3B%20s_c20%3D1397045509015%7C1491653509015%3B%20s_c20_s%3DMore%2520than%25207%2520days%7C1397047309015%3B%20s_c24%3D1400616538424%7C1495224538424%3B%20s_c24_s%3DLess%2520than%25201%2520day%7C1400618338424%3B%20s_gpv_pn%3Dwdpro%252Fwdw%252Fus%252Fen%252Ftools%252Ffinder%252Fdining%252Fmagickingdom%252Fbeourguestrestaurant%7C1400631862919%3B; s_sess=%20s_cc%3Dtrue%3B%20prevPageLoadTime%3Dwdpro%252Fwdw%252Fus%252Fen%252Ftools%252Ffinder%252Fdining%252Fmagickingdom%252Fbeourguestrestaurant%257C5.4%3B%20s_ppv%3D-%252C48%252C45%252C1167%3B%20s_wdpro_lid%3D%3B%20s_sq%3D%3B");
					post.setHeader("Host", "disneyworld.disney.go.com");
					post.setHeader("Origin", "https://disneyworld.disney.go.com");
					post.setHeader("Referer", "https://disneyworld.disney.go.com/dining/magic-kingdom/be-our-guest-restaurant/");
					post.setHeader("User-Agent", "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_8_5) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/35.0.1916.114 Safari/537.36");
					post.setHeader("X-Requested-With", "XMLHttpRequest");
					
					String content = getRequestContent(day, time);
					
			        final ByteArrayEntity entity = new ByteArrayEntity(content.getBytes());
			        entity.setContentType(MediaType.APPLICATION_JSON);
			        entity.setContentEncoding("UTF-8");
			        post.setEntity(entity);
					
			        HttpResponse response = client.execute(post);
			        
			        HttpEntity responseEntity = response.getEntity();
			        
			        String responseContent =  new String(IOUtils.toByteArray(responseEntity.getContent()));
			        
			        System.out.print("Availability for Sept " + day + " at " + time + "\r\n\r\n\r\n");
			        System.out.println(responseContent);
			        System.out.print("\r\n\r\n-----------------------------------------------\r\n\r\n");		        
		
			        if (!responseContent.contains("No tables"))
			        	System.exit(0);
			        	
			        if (time.equalsIgnoreCase("dinner"))
			        	time = "4";
			        else
			        {
			        	time = "dinner";
			        	
			        	++day;
			        	if (day > 8)
			        		day = 1;
			        }
			        Thread.currentThread().sleep(500);
			        
			        responseEntity.getContent().close();
			       
			        

				}
				catch (Exception e)
				{
					//e.printStackTrace();
				}
			}  

	}
	
	private static String getRequestContent(int day, String time)
	{
		
		if (time.equalsIgnoreCase("Dinner"))
			return "searchDate=09%2F0" + day + "%2F2014&skipPricing=true&searchTime=Dinner&partySize=3&id=16660079%3BentityType%3Drestaurant&searchDate=09%2F0" + day + "%2F2014&searchTime=Dinner&partySize=3&type=dining";

		return "searchDate=09%2F0" + day + "%2F2014&skipPricing=true&searchTime=4%3A00+PM&partySize=2&id=16660079%3BentityType%3Drestaurant&searchDate=09%2F0" + day + "%2F2014&searchTime=4%3A00+PM&partySize=2&type=dining";

		
	}
}
