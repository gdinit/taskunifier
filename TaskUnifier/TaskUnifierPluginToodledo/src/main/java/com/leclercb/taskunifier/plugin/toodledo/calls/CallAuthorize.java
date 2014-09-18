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

final class CallAuthorize extends AbstractCall {

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

    /**
     * Example : <token>td12345678901234</token>
     *
     * @param content
     * @return
     * @throws SynchronizerException
     */
    private String getResponseMessage(String content)
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

            Node nToken = childNodes.item(0);

            if (!nToken.getNodeName().equals("token"))
                this.throwResponseError(
                        ToodledoErrorType.ACCOUNT,
                        content,
                        nToken);

            return nToken.getTextContent();
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
