import java.io.File;
import java.io.IOException;
import java.net.URLEncoder;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineEvent;
import javax.sound.sampled.LineListener;
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
			
			
			int day = 8;
			String time = "11:30";
			
			
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
					
					HttpPost post = new HttpPost("https://disneyworld.disney.go.com/finder/dining-availability/");
					
					post.setHeader("Accept", "*/*");
					post.setHeader("Accept-Encoding", "gzip, deflate, br");
					post.setHeader("Accept-Language", "en-US,en;q=0.8");
					post.setHeader("Connection", "keep-alive");
					post.setHeader("Cache-Control", "no-cache");
					//post.setHeader("Content-Length", "178");
					post.setHeader("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");
					post.setHeader("Cookie", "AMCV_espn=MCAID%7C2B69C728051D26A6-600001390000B88F; s_c24=1469913110399; optimizelyEndUserId=oeu1488378866963r0.6655858980421958; ctoVisitor={%22visitorId%22:%221490456998792-1525957505505%22}; cX_G=cx%3Aifs56il9j1pmdx1m%3A1qdd6iimdxq6k; cX_P=ig0qit52dasvif5n; GEOLOCATION_jar=%7B%22region%22%3A%22missouri%22%2C%22country%22%3A%22united+states%22%2C%22zipCode%22%3A%2264117%22%2C%22metro%22%3A%22kansas+city%22%2C%22metroCode%22%3A%22616%22%2C%22countryisocode%22%3A%22USA%22%7D; optimizelySegments=%7B%22166049822%22%3A%22none%22%2C%22166646996%22%3A%22false%22%2C%22167330480%22%3A%22search%22%2C%22167351530%22%3A%22gc%22%2C%224331343716%22%3A%22true%22%2C%228335390320%22%3A%22true%22%2C%224330051480%22%3A%22true%22%7D; optimizelyBuckets=%7B%228465532745%22%3A%228453124693%22%7D; __CG=u%3A1090749961339375600%2Cs%3A1088140065%2Ct%3A1509620177312%2Cc%3A1%2Ck%3Aabc.go.com%2F34%2F34%2F239%2Cf%3A1%2Ci%3A1; AMCV_CB793704532E6E4D0A490D44%40AdobeOrg=2096510701%7CMCAID%7C2B69C728051D26A6-600001390000B88F%7CMCIDTS%7C17473%7CMCMID%7C06638818232191195800519471888511887961%7CMCAAMLH-1503435524%7C9%7CMCAAMB-1509620176%7CNRX38WO0n5BH8Th-nqAG_A%7CMCOPTOUT-1502854611s%7CNONE%7CMCSYNCSOP%7C411-17401%7CvVersion%7C2.0.0; roomForm_jar=%7B%22checkInDate%22%3A%222018-01-07%22%2C%22checkOutDate%22%3A%222018-01-11%22%2C%22numberOfAdults%22%3A%224%22%2C%22numberOfChildren%22%3A%220%22%2C%22accessible%22%3A%220%22%2C%22resort%22%3A%2280010394%3BentityType%3Dresort%22%2C%22roomTypeId%22%3Afalse%2C%22components%22%3A%22%22%2C%22cartId%22%3A%22%22%2C%22cartItemId%22%3A%22%22%2C%22numberOfRooms%22%3A%221%22%7D; currentOffer_jar=%7B%22currentOffer%22%3A%22gift-disney-vacation-package-magic-band%22%7D; wdpro_seen_cmps=/91992/%2C/88952/; DS=Y2VybmVyLmNvbTs7ODA5OTA3OztjZXJuZXIgY29ycG9yYXRpb247A; DE2=dXNhO21vO2thbnNhcyBjaXR5O2Jyb2FkYmFuZDs1OzU7NDs2MTY7MzkuMDA5MzstOTQuNDAyMzs4NDA7MjY7MTAzOzY7dXM7A; CRBLM=CBLM-001:AAAAAAABAE4AAAABAFAAAAABAE0AAAABBLcAAAABB3cAAAABAEgAAAABB4IAAAABAEsAAAABB9EAAAABAzkAAAABBOgAAAABB/EAAAABBFMAAAABCN0AAAABBAgAAAABCRkAAAABCSEAAAABCTIAAAABCVQAAAABCXQAAAABA3oAAAAB; CRBLM_LAST_UPDATE=1509707051:{41231508-470D-4B23-8A73-06575348FC41}; AMCV_EDA101AC512D2B230A490D4C%40AdobeOrg=-1758798782%7CMCAID%7C2B69C728051D26A6-600001390000B88F%7CMCIDTS%7C17476%7CMCMID%7C63653380328050259004545709010393559585%7CMCOPTOUT-1509849792s%7CNONE%7CMCAAMLH-1510447374%7C9%7CMCAAMB-1510447392%7CBxDq_7FVWRkfQS4moeSdmYpvtwaPZ8lZE6zEnXSgOA%7CMCCIDH%7C1139439460; __CT_Data=gpv=37&apv_32300_www07=37&ckp=cd&dm=.disney.go.com; WRUIDB20170404=0; s_vi=[CS]v1|2B69C728051D26A6-600001390000B88F[CE]; SWID={ACA6E4C3-4397-43EF-A4AD-259926923E09}; CART-wdw_jar=%7B%22cartUserIdentifier%22%3A%7B%22type%22%3A%22GUEST%22%2C%22id%22%3A%22%7BACA6E4C3-4397-43EF-A4AD-259926923E09%7D%22%2C%22idType%22%3A%22swid%22%7D%7D; utag_main=_st:1509888703134$ses_id:1509887750589%3Bexp-session; PHPSESSID=s0tqghu8762vmjq84jpv5le3t1; rampPersonalizationDecision=%7B%22personalizationDecision%22%3A%7B%22decision%22%3Atrue%2C%22version%22%3A1%2C%22percentage%22%3A%22100%22%7D%7D; rampexpressCheckoutStandAloneTickets_A=%7B%22expressCheckoutStandAloneTickets%22%3A%7B%22decision%22%3Atrue%2C%22version%22%3A%229%22%2C%22percentage%22%3A%22100%22%7D%7D; rampexpressCheckoutAffiliateStores_A=%7B%22expressCheckoutAffiliateStores%22%3A%7B%22decision%22%3Atrue%2C%22version%22%3A%2214%22%2C%22percentage%22%3A%22100%22%7D%7D; rampExpressCheckoutAccordion_A=%7B%22expressCheckoutAccordion%22%3A%7B%22decision%22%3Afalse%2C%22version%22%3A1%2C%22percentage%22%3A%220%22%7D%7D; mbox=PC#4bd01910dfd348e9921b1404b9b8c57c.20_87#1517683873|check#true#1509907933|session#aa5e649a8f2d49988d92b8b5f4d20db6#1509909733|mboxEdgeServer#mboxedge20.tt.omtrdc.net#1509909733; pep_oauth_token=b0d98a917ec14d13a95b0e5abfe4d6d5; personalization_jar=%7B%22id%22%3A%22565787b6-d4cd-4ce8-836c-9d300dfcbe43%22%7D; bkSent=true; _uetsid=_ueteec8caa9; UNID=19effdc5-fb8a-46d7-97cc-51b28c65c3ae; UNID=19effdc5-fb8a-46d7-97cc-51b28c65c3ae; LANGUAGE_MESSAGE_DISPLAY=2; surveyThreshold_jar=%7B%22pageViewThreshold%22%3A2%7D; LPVID=FhZDE2MzM3NjdkNjI4MDA5; LPSID-65526753=OT4DOZsuQuWDWMGQvO7KdA; _sdsat_enableClickTale=true; boomr_rt=cl=1509907885382&nu=https%3A%2F%2Fdisneyworld.disney.go.com%2Fdining%2Fanimal-kingdom%2Fyak-and-yeti-restaurant%2F%23; localeCookie_jar=%7B%22contentLocale%22%3A%22en_US%22%2C%22version%22%3A%223%22%2C%22precedence%22%3A0%7D; WDPROView=%7B%22version%22%3A2%2C%22preferred%22%3A%7B%22device%22%3A%22desktop%22%2C%22screenWidth%22%3A1680%2C%22screenHeight%22%3A885%2C%22screenDensity%22%3A2%7D%2C%22deviceInfo%22%3A%7B%22device%22%3A%22desktop%22%2C%22screenWidth%22%3A1680%2C%22screenHeight%22%3A885%2C%22screenDensity%22%3A2%7D%7D; localeCookie_jar_aka=%7B%22contentLocale%22%3A%22en_US%22%2C%22version%22%3A%223%22%2C%22precedence%22%3A0%2C%22akamai%22%3A%22true%22%7D; languageSelection_jar_aka=%7B%22preferredLanguage%22%3A%22en-us%22%2C%22version%22%3A%221%22%2C%22precedence%22%3A0%2C%22language%22%3A%22en-us%22%2C%22akamai%22%3A%22true%22%7D; ak_bmsc=47A0EA753AAB99A60A045ADD6954769217D70F44902E0000995DFF598E559C4B~pl70gfixJYUndmjCLSwW+041+huHTk3ij/MuhZu+SyfV1h9p1oRbY4Lcdmu+A2KQpOUWTsZ32WD83ptUEvwRYByVmFmQQhfvDAV44BqkvnqLqiW3VFOKRjA+hMs3pfyNj5BgrlL5BNRaYkIA9KfKce7NC9GxS3BGxt9wb3YdMOZlEeti6uBEA/RXUXJegr4YiHDw4L7nB1kZLKOYJNXseQBgmV/gmHT5WGP+VwELReXRqQLYmbHJKVJ237jUcwGNT2; bm_sv=EAD5AE8E6405E0A8BD48F7F1CCC58424~XLeK03oOzJ+NB+Y10eOtMKmoapPt9AczmqZZlg+aIjmmJYCy/bEDDbuL0tWMurn/N6R+5/GCH//xBpWKfes1onVMVClClU7kNEplPncEL8/H1h5vuMTZY8gVINw8e9RvKuy4vThZArMcO1KahQdXkm1mbIV5zhWzipafmVKEP8g=; s_pers=%20s_c24%3D1465988305546%7C1560596305546%3B%20s_c20%3D1488378867045%7C1582986867045%3B%20s_c20_s%3DMore%2520than%252030%2520days%7C1488380667045%3B%20s_fid%3D3A1C5B09032B7571-126880AEE87FF740%7C1572923949833%3B%20s_gpv_pn%3Dwdpro%252Fwdw%252Fus%252Fen%252Ftools%252Ffinder%252Fdining%252Fanimalkingdom%252Fyakandyetirestaurant%7C1509909701984%3B; s_sess=%20s_cc%3Dtrue%3B%20s_ppv%3D-%252C49%252C37%252C1165%3B%20s_wdpro_lid%3D%3B%20s_sq%3D%3B");
					post.setHeader("Host", "disneyworld.disney.go.com");
					post.setHeader("Origin", "https://disneyworld.disney.go.com");
					post.setHeader("Referer", "https://disneyworld.disney.go.com/dining/animal-kingdom/yak-and-yeti-restaurant/");
					post.setHeader("User-Agent", "Mozilla/5.0 (Linux; Android 6.0; Nexus 5 Build/MRA58N) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/61.0.3163.100 Mobile Safari/537.36");
					post.setHeader("X-Requested-With", "XMLHttpRequest");
					
					String content = getRequestContent(day, time);
					
			        final ByteArrayEntity entity = new ByteArrayEntity(content.getBytes());
			        entity.setContentType(MediaType.APPLICATION_JSON);
			        entity.setContentEncoding("UTF-8");
			        post.setEntity(entity);
					
			        HttpResponse response = client.execute(post);
			        
			        HttpEntity responseEntity = response.getEntity();
			        
			        String responseContent =  new String(IOUtils.toByteArray(responseEntity.getContent()));
			        
			        System.out.print("Availability for Jan " + day + " at " + time + "\r\n\r\n\r\n");
			        System.out.println(responseContent);
			        System.out.print("\r\n\r\n-----------------------------------------------\r\n\r\n");		        
		
			        if (!responseContent.isEmpty() && !responseContent.contains("No tables"))
			        {
			        	playAlert();
			        	System.exit(0);
			        }
			        
			        /*	
			        if (day == 8)
			        {
			        	day = 11;
			        	time = "15:00";
			        }
			        else
			        {
			        	day = 8;
			        	time = "11:00";
			        }
			        Thread.currentThread().sleep(500);
			        */
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

		final String formatDay = String.format("%02d", day);
		final String formatTime = URLEncoder.encode(time);
		
		return "pep_csrf=35ba6d5b8b5cdc928166831f26843ae6d249e7cc4bbd76c165a9da128de0ced2c107ce249b20c1e4ce2864c4903a3e1ff50e1eb0c7d43a122449aafd6fd24e97&searchDate=2018-01-" + formatDay + "&skipPricing=true&searchTime=" + formatTime + "&partySize=4&id=215686%3BentityType%3Drestaurant&type=dining";
		
		//return "pep_csrf=81813d1b5ce864113a7822f72443fcf9434ec5d17f186bdd6d61311571cf069f655344dec592ad5d74cdaae943b871859810cb7c960870471dd1365d6366573c&searchDate=2018-01-" + formatDay + "&skipPricing=true&searchTime=11%3A00&partySize=4&id=215686%3BentityType%3Drestaurant&type=dining";
		
	}
	
	private static class PlayStatus implements LineListener
	{
		public boolean playCompleted = false;
		
	    public void update(LineEvent event) {
	        LineEvent.Type type = event.getType();
	         
	        if (type == LineEvent.Type.START) {
	            System.out.println("Playback started.");
	             
	        } else if (type == LineEvent.Type.STOP) {
	            playCompleted = true;
	            System.out.println("Playback completed.");
	        }
	 
	    }
	}
	
	private static void playAlert()
	{
		try
		{
			final PlayStatus playStatus = new PlayStatus();
			
			File audioFile = new File("./src/test/resources/sound/bazinga.wav");
			
	        AudioInputStream audioStream = AudioSystem.getAudioInputStream(audioFile);
	        
	        AudioFormat format = audioStream.getFormat();
	
	        DataLine.Info info = new DataLine.Info(Clip.class, format);
	
	        Clip audioClip = (Clip) AudioSystem.getLine(info);
	
	        audioClip.addLineListener(playStatus);
	
	        audioClip.open(audioStream);
	         
	        audioClip.start();
	         
	        while (!playStatus.playCompleted) {
	            // wait for the playback completes
	            try {
	                Thread.sleep(1000);
	            } catch (InterruptedException ex) {
	                ex.printStackTrace();
	            }
	        }
	         
	        audioClip.close();
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}
}
