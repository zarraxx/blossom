package cn.net.xyan.blossom.declarative.ui;

import cn.net.xyan.blossom.declarative.utils.ByteCodeUtils;
import cn.net.xyan.blossom.declarative.utils.ClassMetaModel;
import cn.net.xyan.blossom.declarative.utils.MethodMetaModel;
import cn.net.xyan.blossom.declarative.utils.SuperMethodWarpMetaModel;
import com.vaadin.annotations.DesignRoot;
import com.vaadin.shared.util.SharedUtil;
import com.vaadin.ui.Component;
import com.vaadin.ui.declarative.DesignContext;
import com.vaadin.ui.declarative.DesignException;
import javassist.CtClass;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.parser.Parser;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Method;

/**
 * Created by zarra on 16/6/9.
 */
public class SimpleComponentClassFactoryImpl implements ComponentClassFactory {
    @Override
    public ComponentClassMetaModel parse(String className,InputStream in) throws ClassNotFoundException {

        //only need know the tag with _id; and the root tag;

        Document doc = parseXML(in);



        Elements elements =  doc.getElementsByTag("body");
        if (elements.size()!= 1){
            throw new UnsupportedOperationException("the xml document must have only one root element");
        }
        Element body = elements.first();

        elements = body.children();

        if (elements.size()!= 1){
            throw new UnsupportedOperationException("the xml document must have only one root element");
        }
        Element root = elements.first();

        String name = root.nodeName();

        DesignContext designContext = new DesignContext(doc);


        //add package prefix
        elements = doc.getElementsByTag("meta");

        for (Element element:elements){
            String attrName = element.attr("name");
            String attrContent = element.attr("content");
            if ("package-mapping".equals(attrName) && attrContent != null){
                String[] split = attrContent.split(":");
                designContext.addPackagePrefix(split[0],split[1]);
            }
        }

        String rootClass = tagToClass(name,designContext);

        Class<?> parentCls = Class.forName(rootClass);

        ComponentClassMetaModel metaModel = new ComponentClassMetaModel(className, (Class<? extends Component>) parentCls);

        Elements fieldElements = root.getElementsByAttribute("_id");

        for (Element element: fieldElements){
            metaModel.getFields().add(element2Field(element,designContext));
        }
        return metaModel;
    }

    public ClassMetaModel.DynamicFieldInfo element2Field(Element element,DesignContext designContext) throws ClassNotFoundException {
        String tag = element.nodeName();
        String clsName = tagToClass(tag,designContext);

        String fieldName  = element.attr("_id");

        return new ClassMetaModel.DynamicFieldInfo(fieldName,Class.forName(clsName));

    }

    @Override
    public Class<? extends Component> compileToJavaClass(ComponentClassMetaModel componentClassInfo) throws Exception {

        ClassMetaModel classMetaModel = new ClassMetaModel(componentClassInfo.getName(), componentClassInfo.getSurperClass());

        classMetaModel.getFields().addAll(componentClassInfo.getFields());

        classMetaModel.getAnnotations().add(DesignRoot.class);

        CtClass ctClass = ByteCodeUtils.newClass(classMetaModel);

        ctClass = ByteCodeUtils.addStubInterfaceToCls(ctClass);

//        for (ClassMetaModel.DynamicFieldInfo fieldInfo : componentClassInfo.getFields()){
//            ByteCodeUtils.addFieldToCls(ctClass,fieldInfo);
//        }

        for (MethodMetaModel methodMetaModel :componentClassInfo.getMethods()){
            ByteCodeUtils.addMethod(ctClass,methodMetaModel);
        }

        for (Method method:componentClassInfo.getSurperClass().getMethods()){
            SuperMethodWarpMetaModel warp = new SuperMethodWarpMetaModel(method);
            ByteCodeUtils.addMethod(ctClass,warp);
        }

        //ByteCodeUtils.addAnnotationToCls()

        ctClass.debugWriteFile("target");

        return ctClass.toClass();
    }

    public static Document parseXML(InputStream html) {
        try {
            Document doc = Jsoup.parse(html, "UTF-8", "", Parser.htmlParser());
            return doc;
        } catch (IOException e) {
            throw new DesignException("The html document cannot be parsed.");
        }
    }

    public  static DesignContext designToComponentTree(Document doc)throws Exception{
        DesignContext designContext = new DesignContext(doc);

        Class<?> cls = designContext.getClass();
        Method method = cls.getDeclaredMethod("readPackageMappings",Document.class);
        method.setAccessible(true);
        method.invoke(designContext,doc);
//            Method[] methods = cls.getMethods();
//            for (Method method : methods){
//                logger.info(method.getName());
//            }

        return designContext;
        // designContext.readPackageMappings(doc);

    }



    public static String tagToClass(String tagName,DesignContext context){
        // Extract the package and class names.
        // Otherwise, get the full class name using the prefix to package
        // mapping. Example: "vaadin-vertical-layout" ->
        // "com.vaadin.ui.VerticalLayout"
        String[] parts = tagName.split("-", 2);
        if (parts.length < 2) {
            throw new DesignException("The tagname '" + tagName
                    + "' is invalid: missing prefix.");
        }
        String prefixName = parts[0];
        String packageName = context.getPackage(prefixName);
        if (packageName == null) {
            throw new DesignException("Unknown tag: " + tagName);
        }
        String[] classNameParts = parts[1].split("-");
        String className = "";
        for (String classNamePart : classNameParts) {
            // Split will ignore trailing and multiple dashes but that
            // should be
            // ok
            // <vaadin-button--> will be resolved to <vaadin-button>
            // <vaadin--button> will be resolved to <vaadin-button>
            className += SharedUtil.capitalize(classNamePart);
        }
        String qualifiedClassName = packageName + "." + className;

        return qualifiedClassName;
    }
}
