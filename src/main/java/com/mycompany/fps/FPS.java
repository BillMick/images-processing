/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 */

package com.mycompany.fps;
import ij.IJ ;
import ij.ImagePlus;
import ij.process.ImageConverter;
import ij.process.ImageProcessor;
import ij.io.Opener ; 
import ij.gui.NewImage ;
import ij.gui.Roi ; 
import java.io.File;
import ij.process.ImageStatistics ; 

/**
 *
 * @author michael
 */
public class FPS {

    public FPS(String img_path) {
        if(img_path != null)
        {
            Opener img_opener = new Opener() ;
            ImagePlus imp =  img_opener.openImage(img_path) ;
            new ImageConverter(imp).convertToGray8() ;
            ImageProcessor ip = imp.getProcessor() ;
            int height  = ip.getHeight() ; int width = ip.getWidth() ;
            int new_w = 512 ;
            //float new_h = (float)new_w/height ;
            //System.out.println(new_h);
            //imp = imp.resize(new_w, (int)(width*new_h), "average") ;
            ip.filter(ImageProcessor.MEDIAN_FILTER) ;
            imp = new ImagePlus("Image d'origine Redimensionnée et Filtrée", ip.resize(new_w)) ;
            ip = imp.getProcessor() ;
            //ip.findEdges() ;
            
            //ip.medianFilter() ;
            //ip.sharpen();
            //ip.findEdges();
            imp.show() ;
            ImageStatistics stats = imp.getStatistics() ;
            int threshold = ip.getAutoThreshold() ; //ip.getAutoThreshold(ip.getHistogram()) ;
            //ip.getAutoThreshold() ;
            ImagePlus s_imp = NewImage.createByteImage("Sans Arriere",ip.getWidth(), ip.getHeight(),1,NewImage.GRAY8);
            
            System.out.println(ip.getAutoThreshold(ip.getHistogram())) ;
            System.out.println("Seuil sans histo : "+ip.getAutoThreshold());
            System.out.println("Val_arrier_plan "+ip.getBackgroundValue()) ;
            //ip.threshold(threshold);
            //ip.autoThreshold();
            //int w, h = 0 ;
            
            for(int i = 0 ; i < ip.getWidth() ; i++)
            {
                for(int j = 0 ; j < ip.getHeight() ; j++)
                {
                    if (ip.getPixel(i,j) <= threshold) 
                    {
                        s_imp.getProcessor().putPixel(i, j, ip.getPixel(i,j)) ;
                    }
                    
                }
            }
            
            s_imp.show() ;
            //imp.show() ;
            ImagePlus tc_imp  = new ImagePlus("Trame Carree", s_imp.getProcessor().createImage());
            tc_imp.getProcessor().smooth() ;
            tc_imp.show();
            ImagePlus th_imp  = new ImagePlus("Trame Hexagonale", s_imp.getProcessor().createImage());
            for(int i = 1 ; i < s_imp.getProcessor().getWidth()-1 ; i++)
            {
                for(int j = 2 ; j < s_imp.getProcessor().getHeight()-2 ; j++)
                {
                    th_imp.getProcessor().putPixel(i, j, (s_imp.getProcessor().getPixel(i,j-2)+s_imp.getProcessor().getPixel(i-1,j-1)
                            +s_imp.getProcessor().getPixel(i+1,j-1)+ip.getPixel(i-1,j+1)+s_imp.getProcessor().getPixel(i+1,j+1)
                            +s_imp.getProcessor().getPixel(i,j+2))/6) ;
                }
            }
            
            th_imp.show() ;
            //seuillage 
            ImagePlus seuil_imp  = new ImagePlus("Seuillage", s_imp.getProcessor().createImage());
            for(int i = 0 ; i < s_imp.getWidth() ; i++)
            {
                for(int j = 0 ; j < s_imp.getHeight() ; j++)
                {
                    if(s_imp.getProcessor().getPixel(i, j)-th_imp.getProcessor().getPixel(i, j) < s_imp.getProcessor().getPixel(i, j)-tc_imp.getProcessor().getPixel(i, j))
                    {
                        seuil_imp.getProcessor().putPixel(i,j,255) ;
                    }
                    else 
                    {
                        seuil_imp.getProcessor().putPixel(i,j,0) ;
                    }
                }
            }
            
            threshold =  seuil_imp.getProcessor().getAutoThreshold() ;
            for(int i = 0 ; i < ip.getWidth() ; i++)
            {
                for(int j = 0 ; j < ip.getHeight() ; j++)
                {
                    if (ip.getPixel(i,j) >= threshold) 
                    {
                        seuil_imp.getProcessor().putPixel(i, j, 255) ;
                    }
                    
                }
            }
            
            seuil_imp.show() ;
            
            //squelette 
            ImagePlus sql_imp  = new ImagePlus("Squelette", seuil_imp.getProcessor().createImage());
                //ImagePlus sql_imp_next  = new ImagePlus("Squelette", sql_imp.getProcessor().createImage());
                //sql_imp.getProcessor().erode() ;
                
            int ccount = 0 ; int bcount = 0 ;
            for(int i = 1 ; i < s_imp.getWidth()-1 ; i++)
            {
                for(int j = 1 ; j < s_imp.getHeight()-1 ; j++)
                {
                    if(sql_imp.getProcessor().getPixel(i, j) == 0) 
                    {
                        ccount++ ;
                        bcount++ ;
                    }
                }
            }
                //boolean e_g = true ;
                
            do 
            {
                for(int i = 1 ; i < s_imp.getWidth()-1 ; i++)
                {
                    for(int j = 1 ; j < s_imp.getHeight()-1 ; j++)
                    {
                        if(sql_imp.getProcessor().getPixel(i, j) != 0)
                        {
                            //continue ;
                        }
                        else 
                        {
                        
                        int X = 0, Y = 0, B = 0;
                
                        if(sql_imp.getProcessor().getPixel(i-1, j-1)==0)
                        {
                            X--; Y--; B++;
                        }
                        if(sql_imp.getProcessor().getPixel(i, j-1) == 0)
                        {
                            Y--; B++ ;
                        }
                        if(sql_imp.getProcessor().getPixel(i, j+1) == 0)
                        {
                            Y++ ; B++ ;
                        }
                        if(sql_imp.getProcessor().getPixel(i+1, j-1) == 0)
                        {
                            X++; Y-- ; B++ ;
                        }
                        if(sql_imp.getProcessor().getPixel(i-1, j) == 0)
                        {
                            X-- ; B++ ;
                        }
                        if(sql_imp.getProcessor().getPixel(i+1, j) == 0)
                        {
                            X++ ;  B++ ;
                        }
                        if(sql_imp.getProcessor().getPixel(i-1, j+1) == 0)
                        {
                            X-- ; Y++; B++ ;
                        }
                        if(sql_imp.getProcessor().getPixel(i+1, j+1) == 0)
                        {
                            X++ ; Y++; B++ ;
                        }
                        int F = Math.abs(X) + Math.abs(Y);
                        
                        if(F == 4)
                        {
                            sql_imp.getProcessor().putPixel(i,j, 255);
                            
                        }
                
                        else if((F==3 && (B == 2) || (B==3 && Math.max(Math.abs(X), Math.abs(Y)) == 3)))
                        {
                            sql_imp.getProcessor().putPixel(i,j, 255);
                        }
                        else if(F==3 && B==5 && Math.max(Math.abs(X), Math.abs(Y)) == 3)
                        {
                            sql_imp.getProcessor().putPixel(i,j, 255);
                            
                        }
                    }
                }
                
                }
                bcount = ccount ;
                ccount = 0 ;
                for(int i = 1 ; i < s_imp.getWidth()-1 ; i++)
                {
                    for(int j = 1 ; j < s_imp.getHeight()-1 ; j++)
                    {
                            if(sql_imp.getProcessor().getPixel(i, j) == 0) 
                            {
                                ccount++ ;
                            }
                    }
                }
                System.out.println("C et B "+ccount+" "+bcount) ;
            } while(bcount != ccount) ;
                        
                    
            for(int i = 1 ; i < s_imp.getWidth()-1 ; i++)
            {
                for(int j = 1 ; j < s_imp.getHeight()-1 ; j++)
                {
                    if(sql_imp.getProcessor().getPixel(i, j) !=0)
                    {
                        continue ;
                    }
                    else 
                    {
                        int X = 0, Y = 0, B = 0;
                        if(sql_imp.getProcessor().getPixel(i-1, j-1)==0)
                        {
                            X--; Y--; B++;
                        }
                        if(sql_imp.getProcessor().getPixel(i, j-1) == 0)
                        {
                            Y--; B++ ;
                        }
                        if(sql_imp.getProcessor().getPixel(i, j+1) == 0)
                        {
                            Y++ ; B++ ;
                        }
                        if(sql_imp.getProcessor().getPixel(i+1, j-1) == 0)
                        {
                            X++; Y-- ; B++ ;
                        }
                        if(sql_imp.getProcessor().getPixel(i-1, j) == 0)
                        {
                            X-- ; B++ ;
                        }
                        if(sql_imp.getProcessor().getPixel(i+1, j) == 0)
                        {
                            X++ ;  B++ ;
                        }
                        if(sql_imp.getProcessor().getPixel(i-1, j+1) == 0)
                        {
                            X-- ; Y++; B++ ;
                        }
                        if(sql_imp.getProcessor().getPixel(i+1, j+1) == 0)
                        {
                            X++ ; Y++; B++ ;
                        }
                        if(Math.abs(X)== 1 && Math.abs(Y) == 1 && B ==2 || ((B==3) && Math.abs(X) == 0 && Math.abs(Y) == 2)|| ((B==3) && Math.abs(X)==2  && Math.abs(Y) == 0))
                        {
                            sql_imp.getProcessor().putPixel(i,j, 255);
                        }
                    }
                }
            }
            
            sql_imp.show();
            // detection des minuties
            ImagePlus dm_imp = new ImagePlus("Minuties", sql_imp.getProcessor().createImage());
            
            ImageProcessor c = dm_imp.getProcessor() ;
            
            for(int f = 1 ; f < dm_imp.getWidth()-1; f++)
            {
                for(int g = 1 ; g < dm_imp.getHeight()-1 ; g++)
                {
                    int V[] = new int[9];
                    int CN;
                    V[0] = c.get(f-1,g);   if(V[0] == 255) {V[0] = 1;}
                    V[1] = c.get(f-1,g-1); if(V[1] == 255) {V[1] = 1;}
                    V[2] = c.get(f,g-1);   if(V[2] == 255) {V[2] = 1;}
                    
                    V[3] = c.get(f+1,g-1); if(V[3] == 255) {V[3] = 1;}
                    V[4] = c.get(f+1,g);   if(V[4] == 255) {V[4] = 1;}
                    V[5] = c.get(f+1,g+1); if(V[5] == 255) {V[5] = 1;}
                    
                    V[6] = c.get(f ,g+1);  if(V[6] == 255) {V[6] = 1;}
                    V[7] = c.get(f-1,g+1); if(V[7] == 255) {V[7] = 1;}	
                    V[8] = c.get(f-1,g);   if(V[8] == 255) {V[8] = 1;}
                    CN = (Math.abs(V[1]-V[0])+Math.abs(V[2]-V[1])+ Math.abs(V[3]-V[2])+ Math.abs(V[4]-V[3])
                        + Math.abs(V[5]-V[4])+ Math.abs(V[6]-V[5])+ Math.abs(V[7]-V[6])+ Math.abs(V[8]-V[7]) )/2;
                    if(CN == 1 || CN ==3)
                    {
                        dm_imp.getProcessor().drawOval(f+1,g+1, 8,8) ;
                    }
                }
            }
            
            dm_imp.show();   
        }
    }
    
    public static void main(String[] args) {
        System.out.println("Hello World!");
        new FPS("") ;
    }
}
