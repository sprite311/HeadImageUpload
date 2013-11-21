package com.pkg.upload;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;


/**
 * discuz头像上传  集成camera.swf 可对图片进行裁剪及调用摄像头
 * @author quadrapop
 *
 */
public class AvatarServlet extends HttpServlet{

	private static final long serialVersionUID = -6611495029909662649L;
	private String tempPath = "";
	private String imgPath = "";

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		this.doPost(req, resp);
	}

	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		response.addHeader("Content-Type", "text/html;charset=utf-8");
		PrintWriter out = response.getWriter();
		String imagepath="";
			
		String action= request.getParameter("a");
		String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+"/";;
		this.tempPath = getPhysicalPath(request,"upload/img_cache/");
		this.imgPath = getPhysicalPath(request,"upload/img/");
		
		if(action==null){
			
			//最终裁剪好的图片存放位置
			String uid=UUID.randomUUID().toString();
			
			imagepath="upload/img/"+uid+"_big.jpg";		// 裁剪后的路径，也就是页面上要显示的图片路径
			
			/*
			 * function updateavatar()  是裁剪成功后的回调函数
			 */
			
	        out.println("<script type=\"text/javascript\">");
	        out.println("function updateavatar() {");
	        out.println("document.getElementById(\"avatarpic_big\").src='"+imagepath+"';");
	        imagepath="upload/img/"+uid+"_middle.jpg";
	        out.println("document.getElementById(\"avatarpic_middle\").src='"+imagepath+"';");
	        imagepath="upload/img/"+uid+"_small.jpg";
	        out.println("document.getElementById(\"avatarpic_small\").src='"+imagepath+"';");
	        out.println("}");
	        out.println("</script>");
	        
			try {
				out.print(renderHtml("5",basePath,URLEncoder.encode(uid,"utf-8")));
			} catch (Exception e) {
				e.printStackTrace();
			}
			
		}else if("uploadavatar".equals(action)){ //上传临时图片
			String picNewName= "";
			DiskFileItemFactory fac = new DiskFileItemFactory();
			fac.setRepository(new File(this.tempPath));
			ServletFileUpload upload = new ServletFileUpload(fac);
			upload.setHeaderEncoding("utf-8");
			List fileList = null;
			try {
				fileList = upload.parseRequest(request);
			} catch (FileUploadException ex) {
				ex.printStackTrace();
			}
			Iterator<FileItem> it = fileList.iterator();
			String name = "";
			String extName = "";
			//判断存贮路径是否存在
			File f=new File(tempPath);
			if(!f.exists()){
				f.mkdirs();
			}
			
			while (it.hasNext()) {
				FileItem item = it.next();
				if (!item.isFormField()) {
					name = item.getName();
					if (name == null || name.trim().equals("")) {
						continue;
					}
					// 扩展名格式：
					if (name.lastIndexOf(".") >= 0) {
						extName = name.substring(name.lastIndexOf("."));
					}
					File file = null;
					picNewName= UUID.randomUUID().toString()+extName;
					file = new File( tempPath + picNewName);
					if(file.exists()){
						file.delete();
					}
					File saveFile = new File(tempPath+ picNewName);
					try {
						item.write(saveFile);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
			out.print(basePath+"upload/img_cache/"+picNewName);
		}else if("rectavatar".equals(action)){//缩略图
			
			String avatar1 = request.getParameter("avatar1");//大
			String avatar2 = request.getParameter("avatar2");//中
			String avatar3 = request.getParameter("avatar3");//小
			String output ="";
			try {
			output = URLDecoder.decode(request.getParameter("input").trim(),"utf-8");
			}catch(Exception e)
			{
				System.out.println("解码错误!");
			}
			imagepath=imgPath+output+"_big.jpg";
			boolean a1=saveFile(imagepath,getFlashDataDecode(avatar1));
			imagepath=imgPath+output+"_middle.jpg";
			boolean a2=saveFile(imagepath,getFlashDataDecode(avatar2));
			imagepath=imgPath+output+"_small.jpg";
			boolean a3=saveFile(imagepath,getFlashDataDecode(avatar3));
			
			if(a1&&a2&&a3){
				out.print("<?xml version=\"1.0\" ?><root><face success=\"1\"/></root>");
			}else{
				out.print("<root><message type=\"error\" value=\"-1\" /></root>");
			}
		}
		out.flush();
	}

	//编辑页面中包含 camera.swf 的 HTML 代码
	public String renderHtml(String id,String basePath,String input) throws Exception
	{
		String uc_api =URLEncoder.encode(basePath+"avatar.jhtml","utf-8");
		String urlCameraFlash = "js/camera.swf?nt=1&inajax=1&appid=1&input="+input+"&uploadSize=1000&ucapi="+uc_api;
		urlCameraFlash = "<script src=\"js/common.js?B6k\" type=\"text/javascript\"></script><script type=\"text/javascript\">document.write(AC_FL_RunContent(\"width\",\"450\",\"height\",\"253\",\"scale\",\"exactfit\",\"src\",\""+urlCameraFlash+"\",\"id\",\"mycamera\",\"name\",\"mycamera\",\"quality\",\"high\",\"bgcolor\",\"#ffffff\",\"wmode\",\"transparent\",\"menu\",\"false\",\"swLiveConnect\",\"true\",\"allowScriptAccess\",\"always\"));</script>";
		return urlCameraFlash;
	}
	
	// 获取裁剪后的字节
	private byte[] getFlashDataDecode(String src)
	{
		char []s=src.toCharArray();
		int len=s.length;
	    byte[] r = new byte[len / 2];
	    for (int i = 0; i < len; i = i + 2)
	    {
	        int k1 = s[i] - 48;
	        k1 -= k1 > 9 ? 7 : 0;
	        int k2 = s[i + 1] - 48;
	        k2 -= k2 > 9 ? 7 : 0;
	        r[i / 2] = (byte)(k1 << 4 | k2);
	    }
	    return r;
	}
	
	// 保存文件
	public boolean saveFile(String path,byte[]b){
		try{
			FileOutputStream fs = new FileOutputStream(path);
		    fs.write(b, 0, b.length);
		    fs.close();
			return true;
		}catch(Exception e){
		    return false;
		}
	}
	
	/**
	 * 根据传入的虚拟路径获取物理路径
	 * 
	 * @param path
	 * @return
	 */
	private String getPhysicalPath(HttpServletRequest request,String path) {
		String servletPath = request.getServletPath();
		String realPath = request.getSession().getServletContext()
				.getRealPath(servletPath);
		return new File(realPath).getParent() +"\\" +path;
	}
}
