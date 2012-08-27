package org.nhindirect.gateway.testutils;

import org.w3c.dom.Attr;
import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.TypeInfo;
import org.w3c.dom.UserDataHandler;

public class ElementAdapter implements Element {

    public String getAttribute(String name) {
        return null;
    }

    public String getAttributeNS(String namespaceURI, String localName) throws DOMException {
        return null;
    }

    public Attr getAttributeNode(String name) {
        return null;
    }

    public Attr getAttributeNodeNS(String namespaceURI, String localName) throws DOMException {
        return null;
    }

    public NodeList getElementsByTagName(String name) {
        return null;
    }

    public NodeList getElementsByTagNameNS(String namespaceURI, String localName) throws DOMException {
        return null;
    }

    public TypeInfo getSchemaTypeInfo() {
        return null;
    }

    public String getTagName() {
        return null;
    }

    public boolean hasAttribute(String name) {
        return false;
    }

    public boolean hasAttributeNS(String namespaceURI, String localName) throws DOMException {
        return false;
    }

    public void removeAttribute(String name) throws DOMException {
    }

    public void removeAttributeNS(String namespaceURI, String localName) throws DOMException {
    }

    public Attr removeAttributeNode(Attr oldAttr) throws DOMException {
        return null;
    }

    public void setAttribute(String name, String value) throws DOMException {
    }

    public void setAttributeNS(String namespaceURI, String qualifiedName, String value) throws DOMException {
    }

    public Attr setAttributeNode(Attr newAttr) throws DOMException {
        return null;
    }

    public Attr setAttributeNodeNS(Attr newAttr) throws DOMException {
        return null;
    }

    public void setIdAttribute(String name, boolean isId) throws DOMException {
    }

    public void setIdAttributeNS(String namespaceURI, String localName, boolean isId) throws DOMException {
    }

    public void setIdAttributeNode(Attr idAttr, boolean isId) throws DOMException {
    }

    public Node appendChild(Node newChild) throws DOMException {
        return null;
    }

    public Node cloneNode(boolean deep) {
        return null;
    }

    public short compareDocumentPosition(Node other) throws DOMException {
        return 0;
    }

    public NamedNodeMap getAttributes() {
        return null;
    }

    public String getBaseURI() {
        return null;
    }

    public NodeList getChildNodes() {
        return null;
    }

    public Object getFeature(String feature, String version) {
        return null;
    }

    public Node getFirstChild() {
        return null;
    }

    public Node getLastChild() {
        return null;
    }

    public String getLocalName() {
        return null;
    }

    public String getNamespaceURI() {
        return null;
    }

    public Node getNextSibling() {
        return null;
    }

    public String getNodeName() {
        return null;
    }

    public short getNodeType() {
        return 0;
    }

    public String getNodeValue() throws DOMException {
        return null;
    }

    public Document getOwnerDocument() {
        return null;
    }

    public Node getParentNode() {
        return null;
    }

    public String getPrefix() {
        return null;
    }

    public Node getPreviousSibling() {
        return null;
    }

    public String getTextContent() throws DOMException {
        return null;
    }

    public Object getUserData(String key) {
        return null;
    }

    public boolean hasAttributes() {
        return false;
    }

    public boolean hasChildNodes() {
        return false;
    }

    public Node insertBefore(Node newChild, Node refChild) throws DOMException {
        return null;
    }

    public boolean isDefaultNamespace(String namespaceURI) {
        return false;
    }

    public boolean isEqualNode(Node arg) {
        return false;
    }

    public boolean isSameNode(Node other) {
        return false;
    }

    public boolean isSupported(String feature, String version) {
        return false;
    }

    public String lookupNamespaceURI(String prefix) {
        return null;
    }

    public String lookupPrefix(String namespaceURI) {
        return null;
    }

    public void normalize() {
    }

    public Node removeChild(Node oldChild) throws DOMException {
        return null;
    }

    public Node replaceChild(Node newChild, Node oldChild) throws DOMException {
        return null;
    }

    public void setNodeValue(String nodeValue) throws DOMException {
    }

    public void setPrefix(String prefix) throws DOMException {
    }

    public void setTextContent(String textContent) throws DOMException {
    }

    public Object setUserData(String key, Object data, UserDataHandler handler) {
        return null;
    }

}
