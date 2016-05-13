package fr.tpt.atlanalyser.ui.popup.actions;

import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.emf.henshin.model.Formula;
import org.eclipse.emf.henshin.model.HenshinFactory;
import org.eclipse.emf.henshin.model.NestedCondition;
import org.eclipse.emf.henshin.model.Rule;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IObjectActionDelegate;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.console.MessageConsole;

import fr.tpt.atlanalyser.ui.ATLAnalyserActivator;
import fr.tpt.atlanalyser.utils.HenshinFormulaToLatex;

public class RuleToLatex implements IObjectActionDelegate {

    private IStructuredSelection selected;
    private Shell                shell;

    public RuleToLatex() {
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
            Rule rule = (Rule) selected.getFirstElement();

            String latex = HenshinFormulaToLatex.getHeader();

            NestedCondition dummyNc = HenshinFactory.eINSTANCE.createNestedCondition();

            dummyNc.setConclusion(EcoreUtil.copy(rule.getLhs()));

            latex += "\\begin{align*}\n";
            latex += HenshinFormulaToLatex.toLatex(dummyNc);
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
