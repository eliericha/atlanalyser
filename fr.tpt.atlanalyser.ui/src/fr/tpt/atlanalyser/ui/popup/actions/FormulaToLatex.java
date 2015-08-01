package fr.tpt.atlanalyser.ui.popup.actions;

import org.eclipse.core.resources.IFile;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.henshin.model.Formula;
import org.eclipse.emf.henshin.model.Module;
import org.eclipse.emf.henshin.model.Rule;
import org.eclipse.emf.henshin.model.resource.HenshinResourceSet;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IObjectActionDelegate;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.console.MessageConsole;

import fr.tpt.atlanalyser.ui.ATLAnalyserActivator;
import fr.tpt.atlanalyser.utils.HenshinFormulaToLatex;

public class FormulaToLatex implements IObjectActionDelegate {

    private IStructuredSelection selected;
    private Shell                shell;

    public FormulaToLatex() {
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
            Formula formula = (Formula) selected.getFirstElement();

            String latex = HenshinFormulaToLatex.getHeader();
            latex += "\\begin{align*}\n";
            latex += HenshinFormulaToLatex.toLatex(formula);
            latex += " \\end{align*}\n";

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
