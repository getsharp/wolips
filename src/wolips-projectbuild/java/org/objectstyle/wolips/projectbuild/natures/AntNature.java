/* ====================================================================
 * 
 * The ObjectStyle Group Software License, Version 1.0 
 *
 * Copyright (c) 2002 The ObjectStyle Group 
 * and individual authors of the software.  All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer. 
 *
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in
 *    the documentation and/or other materials provided with the
 *    distribution.
 *
 * 3. The end-user documentation included with the redistribution, if
 *    any, must include the following acknowlegement:  
 *       "This product includes software developed by the 
 *        ObjectStyle Group (http://objectstyle.org/)."
 *    Alternately, this acknowlegement may appear in the software itself,
 *    if and wherever such third-party acknowlegements normally appear.
 *
 * 4. The names "ObjectStyle Group" and "Cayenne" 
 *    must not be used to endorse or promote products derived
 *    from this software without prior written permission. For written 
 *    permission, please contact andrus@objectstyle.org.
 *
 * 5. Products derived from this software may not be called "ObjectStyle"
 *    nor may "ObjectStyle" appear in their names without prior written
 *    permission of the ObjectStyle Group.
 *
 * THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED.  IN NO EVENT SHALL THE OBJECTSTYLE GROUP OR
 * ITS CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF
 * USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT
 * OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF
 * SUCH DAMAGE.
 * ====================================================================
 *
 * This software consists of voluntary contributions made by many
 * individuals on behalf of the ObjectStyle Group.  For more
 * information on the ObjectStyle Group, please see
 * <http://objectstyle.org/>.
 *
 */

package org.objectstyle.wolips.projectbuild.natures;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.eclipse.core.resources.ICommand;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.resources.IProjectNature;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.objectstyle.wolips.projectbuild.WOProjectBuildConstants;


/**
 * @author mnolte
 *
 */
public class AntNature implements IProjectNature, WOProjectBuildConstants {
	private IProject project;
	/**
	 * Constructor for WOApplicationNature.
	 */
	public AntNature() {
		super();
	}

	/**
	  * @see org.eclipse.core.resources.IProjectNature#configure()
	  */
	 public void configure() throws CoreException {
	   IProject project = getProject();

	   System.out.println("configure - "+project);
    
	   IProjectDescription desc = project.getDescription();
    
	   ICommand bc[] = desc.getBuildSpec();
    
	   boolean found = false;
    
	   for (int i = 0; i < bc.length; i++) {
		 if (bc[i].getBuilderName().equals (ANT_BUILDER_ID)) {
		   found = true;
		 }
	   }
   if (!found) {
		 List buildCommands = new ArrayList(Arrays.asList(bc));
		 ICommand newCommand = desc.newCommand();
		 newCommand.setBuilderName(ANT_BUILDER_ID);
		 buildCommands.add(newCommand);
		 desc.setBuildSpec((ICommand[])buildCommands.toArray(new ICommand[buildCommands.size()]));
		 project.setDescription(desc, null);
	   }
   }

	 /**
	  * @see org.eclipse.core.resources.IProjectNature#deconfigure()
	  */
	 public void deconfigure() throws CoreException {
	   IProject project = getProject();
    
	   System.out.println("deconfigure - "+project);
	   IProjectDescription desc = project.getDescription();
    
	   ICommand bc[] = desc.getBuildSpec();
    
	   ICommand found = null;
    
	   for (int i = 0; i < bc.length; i++) {
		 if (bc[i].getBuilderName().equals (ANT_BUILDER_ID)) {
		   found = bc[i];
		 }
	   }
    
	   if (null != found) {
		 List buildCommands = new ArrayList(Arrays.asList(bc));
		 buildCommands.remove(found);
		 desc.setBuildSpec((ICommand[])buildCommands.toArray(new ICommand[buildCommands.size()]));
		 project.setDescription(desc, null);
	   }
    }
	/**
	 * @see org.eclipse.core.resources.IProjectNature#getProject()
	 */
	public IProject getProject() {
		return project;
	}

	/**
	 * @see org.eclipse.core.resources.IProjectNature#setProject(IProject)
	 */
	public void setProject(IProject project) {
		this.project = project;
	}
	public static String getNature(boolean isFramework) {
		if(isFramework)
		return ANT_FRAMEWORK_NATURE_ID;
		return ANT_APPLICATION_NATURE_ID;
	}
	
	public static void s_addToProject (IProject project, boolean isFramework) throws CoreException {
	  IProjectDescription desc = project.getDescription();
    
	  String natures_array[] = desc.getNatureIds();
      List natures = new ArrayList(Arrays.asList(natures_array));
	  if (!natures.contains(getNature(isFramework))) {
		natures.add (getNature(isFramework));
		natures_array = (String[])natures.toArray(new String[natures.size()]);
		desc.setNatureIds(natures_array);
		s_setDescription (project, desc);
	  }
	}
  
	public static void s_removeFromProject (IProject project, boolean isFramework) throws CoreException {
	  IProjectDescription desc = project.getDescription();
    
	  String natures_array[] = desc.getNatureIds();
    
	  List natures = new ArrayList(Arrays.asList(natures_array));
    
	  if (natures.contains(getNature(isFramework))) {
		natures.remove (getNature(isFramework));
		natures_array = (String[])natures.toArray(new String[natures.size()]);
		desc.setNatureIds(natures_array);
		s_setDescription (project, desc);
	  }
	}

	private static void s_setDescription (final IProject f_project, final IProjectDescription f_desc) {
	  s_showProgress(
		new IRunnableWithProgress () {
		  public void run(IProgressMonitor pm) {
			try {
			  f_project.setDescription(f_desc, pm);
			} catch (CoreException up) {
			  pm.done();
			}
		  }
		}
	  );
	}
	public static void  s_showProgress(IRunnableWithProgress rwp) {
	  IWorkbench workbench = PlatformUI.getWorkbench();
	  Shell shell = null;
	  if (null != workbench) {
		IWorkbenchWindow window = workbench.getActiveWorkbenchWindow();
		if (null != window) {
		  shell = window.getShell();
		}
	  }
    
	  ProgressMonitorDialog pmd = new ProgressMonitorDialog(shell);
    
	  try {
		pmd.run (true, true, rwp);
	  } catch (InvocationTargetException e) {
		// handle exception
		e.printStackTrace ();
	  } catch (InterruptedException e) {
		// handle cancelation
		e.printStackTrace ();
	  }
	}
}
