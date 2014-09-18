/*
 * TaskUnifier
 * Copyright (c) 2013, Benjamin Leclerc
 * All rights reserved.
 */
package com.leclercb.taskunifier.plugin.toodledo.calls;

import com.leclercb.commons.api.utils.CheckUtils;
import com.leclercb.taskunifier.api.synchronizer.exc.SynchronizerException;
import com.leclercb.taskunifier.api.synchronizer.exc.SynchronizerHttpException;
import com.leclercb.taskunifier.api.synchronizer.exc.SynchronizerParsingException;
import com.leclercb.taskunifier.plugin.toodledo.ToodledoApi;
import com.leclercb.taskunifier.plugin.toodledo.calls.ToodledoErrors.ToodledoErrorType;
import org.apache.http.NameValuePair;
import org.apache.http.client.utils.URIUtils;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.message.BasicNameValuePair;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.StringReader;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

final class CallOAuth extends AbstractCall {

    public URI getAuthorizeUrl() throws SynchronizerException {
        List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("response_type", "code"));
        params.add(new BasicNameValuePair("client_id", ToodledoApi.getInstance().getClientId()));
        params.add(new BasicNameValuePair("state", UUID.randomUUID().toString()));
        params.add(new BasicNameValuePair("scope", "basic tasks notes write"));

        String scheme = super.getScheme();

        try {
            return URIUtils.createURI(
                    scheme,
                    ToodledoApi.getInstance().getApiUrl(),
                    -1,
                    "/3/account/authorize.php",
                    URLEncodedUtils.format(params, "UTF-8"),
                    null);
        } catch (Exception e) {
            throw new SynchronizerHttpException(false, 0, e.getMessage(), e);
        }
    }

    public ToodledoToken getAccessToken(String code) throws SynchronizerException {
        CheckUtils.isNotNull(code);

        List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("grant_type", "authorization_code"));
        params.add(new BasicNameValuePair("code", code));
        params.add(new BasicNameValuePair("vers", "" + ToodledoApi.getInstance().getVersion()));
        if (ToodledoApi.getInstance().getDevice() != null)
            params.add(new BasicNameValuePair("device", "" + ToodledoApi.getInstance().getDevice()));
        params.add(new BasicNameValuePair("os", "" + ToodledoApi.getInstance().getOS()));
        params.add(new BasicNameValuePair("f", "xml"));

        String scheme = super.getScheme();
        String content = super.callGet(scheme, "/3/account/token.php", params);

        return this.getResponseMessage(content);
    }

    /**
     * Example : <token>td12345678901234</token>
     *
     * @param content
     * @return
     * @throws com.leclercb.taskunifier.api.synchronizer.exc.SynchronizerException
     */
    private ToodledoToken getResponseMessage(String content)
            throws SynchronizerException {
        CheckUtils.isNotNull(content);

        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            factory.setIgnoringComments(true);

            DocumentBuilder builder = factory.newDocumentBuilder();
            StringReader reader = new StringReader(content);
            InputSource inputSource = new InputSource(reader);
            Document document = builder.parse(inputSource);
            reader.close();
            NodeList childNodes = document.getChildNodes();

            if (!childNodes.item(0).getNodeName().equals("response"))
                this.throwResponseError(
                        ToodledoErrorType.GENERAL,
                        content,
                        childNodes.item(0));

            NodeList nResponse = childNodes.item(0).getChildNodes();

            ToodledoToken token = new ToodledoToken();

            for (int i = 0; i < nResponse.getLength(); i++) {
                Node nInfo = nResponse.item(i);

                if (nInfo.getNodeName().equals("access_token"))
                    token.setRefreshToken(nInfo.getTextContent());

                if (nInfo.getNodeName().equals("expires_in"))
                    token.setExpiresIn(Integer.parseInt(nInfo.getTextContent()));

                if (nInfo.getNodeName().equals("token_type"))
                    token.setTokenType(nInfo.getTextContent());

                if (nInfo.getNodeName().equals("scope"))
                    token.setScope(nInfo.getTextContent());

                if (nInfo.getNodeName().equals("refresh_token"))
                    token.setRefreshToken(nInfo.getTextContent());
            }

            return token;
        } catch (SynchronizerException e) {
            throw e;
        } catch (Exception e) {
            throw new SynchronizerParsingException(
                    "Error while parsing response ("
                            + this.getClass().getName()
                            + ")",
                    content,
                    e);
        }
    }

}
