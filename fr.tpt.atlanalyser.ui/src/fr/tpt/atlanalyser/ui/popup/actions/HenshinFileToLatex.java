package fr.tpt.atlanalyser.ui.popup.actions;

import org.eclipse.core.resources.IFile;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.henshin.model.Module;
import org.eclipse.emf.henshin.model.resource.HenshinResourceSet;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IObjectActionDelegate;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.console.MessageConsole;

import fr.tpt.atlanalyser.ui.ATLAnalyserActivator;
import fr.tpt.atlanalyser.utils.HenshinFormulaToLatex;

public class HenshinFileToLatex implements IObjectActionDelegate {

    private Shell                shell;
    private IStructuredSelection selected;

    /**
     * Constructor for Action1.
     */
    public HenshinFileToLatex() {
        super();
    }

    /**
     * @see IObjectActionDelegate#setActivePart(IAction, IWorkbenchPart)
     */
    public void setActivePart(IAction action, IWorkbenchPart targetPart) {
        shell = targetPart.getSite().getShell();
    }

    /**
     * @see IActionDelegate#run(IAction)
     */
    public void run(IAction action) {
        if (selected != null) {
            IFile henshinFile = (IFile) selected.getFirstElement();

            String inputHenshin = henshinFile.getLocation().toString();

            HenshinResourceSet resSet = new HenshinResourceSet();
            Resource resource = resSet.getResource(inputHenshin);

            Module module = (Module) resource.getContents().get(0);

            String latex = HenshinFormulaToLatex.toLatex(module);

            MessageConsole console = ATLAnalyserActivator.getConsole();

            console.activate();

            console.clearConsole();

            console.newMessageStream().print(latex);
        }
    }

    /**
     * @see IActionDelegate#selectionChanged(IAction, ISelection)
     */
    public void selectionChanged(IAction action, ISelection selection) {
        if (selection instanceof IStructuredSelection) {
            selected = (IStructuredSelection) selection;
        }
    }

}
