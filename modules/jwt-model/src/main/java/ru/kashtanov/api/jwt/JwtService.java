package ru.kashtanov.api.jwt;

import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.PropsUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.json.JSONFactoryUtil;
import com.liferay.portal.kernel.json.JSONObject;
import org.osgi.service.component.annotations.Component;

import javax.servlet.Servlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.Base64;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;


@Component(
        service = Servlet.class,
        property = {
                "osgi.http.whiteboard.servlet.pattern=/get-jwt"
        }
)
public class JwtService extends HttpServlet {


// Servlet is a class that process request, directly it is not invoked. It is maneged via the Servlet-Container (Tomcat,Jetty etc.)
    // in doGet Servlet-container creates HttpServletRequest, HttpServletResponse objects, an
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws  IOException {
        long userId = PortalUtil.getUserId(request);

        //userId validation .!!!NOTE    Liferay by itself retrieve userId form JSESSION
        if (userId <= 0) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().print("{\"error\":\"Not logged in\"}");
            return;
        }
        // props reading
        String jwtSecret = PropsUtil.get("headless.jwt.secret");
        if (Validator.isBlank(jwtSecret)) { // check if it is not empty
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().print("{\"error\":\"JWT secret not configured\"}");
            return;
        }

        try {
            long now = System.currentTimeMillis();
            String header = "{\"alg\":\"HS256\",\"typ\":\"JWT\"}";
            String payload = String.format(
                    "{\"sub\":\"%d\",\"iat\":%d,\"exp\":%d}",
                    userId, now / 1000, (now + 3600000) / 1000 // 1 час жизни
            );
                 System.out.println("POINT_1, header: "+ header);
                 System.out.println("POINT_2, payload: "+ payload);
            String headerB64 = Base64.getEncoder().encodeToString(header.getBytes("UTF-8"));
            String payloadB64 = Base64.getEncoder().encodeToString(payload.getBytes("UTF-8"));
            String signingInput = headerB64 + "." + payloadB64;
                  System.out.println("POINT_3, signingInput: "+ signingInput);

            Mac hmac = Mac.getInstance("HmacSHA256"); // Hash-based Message Authentication Code
            hmac.init(new SecretKeySpec(jwtSecret.getBytes("UTF-8"), "HmacSHA256"));
            byte[] signature = hmac.doFinal(signingInput.getBytes("UTF-8"));
                  System.out.println("POINT_4, byte[] signature: "+ Arrays.toString(signature));
            String signatureB64 = Base64.getEncoder().encodeToString(signature);
                  System.out.println("POINT_5, signatureB64: "+ signatureB64);
            String jwt = signingInput + "." + signatureB64;
                  System.out.println("POINT_6, signingInput + \".\" + signatureB64: "+ jwt);
            JSONObject json = JSONFactoryUtil.createJSONObject();
            json.put("jwt", jwt.replaceAll("=", "")); // убираем padding для чистоты
                  System.out.println("POINT_7, JSONObject: "+ json);
            response.setContentType("application/json");

            PrintWriter out = response.getWriter();
            out.print(json.toString());

        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().print("{\"error\":\"JWT generation failed\"}");
            e.printStackTrace();
        }
    }
}