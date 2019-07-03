/*
 * uDig - User Friendly Desktop Internet GIS client
 * (C) MangoSystem - www.mangosystem.com 
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Refractions BSD
 * License v1.0 (http://udig.refractions.net/files/bsd3-v10.html).
 */
package org.locationtech.udig.processingtoolbox.internal.ui;

import java.util.Map;
import java.util.logging.Logger;

import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Widget;
import org.geotools.data.Parameter;
import org.geotools.filter.text.cql2.CQLException;
import org.geotools.filter.text.ecql.ECQL;
import org.geotools.process.spatialstatistics.core.Params;
import org.geotools.util.logging.Logging;
import org.locationtech.udig.processingtoolbox.ToolboxPlugin;
import org.locationtech.udig.project.IMap;
import org.opengis.filter.expression.Expression;

/**
 * Expression control
 * 
 * @author Minpa Lee, MangoSystem
 * 
 * @source $URL$
 */
public class ExpressionWidget extends AbstractToolboxWidget {
    protected static final Logger LOGGER = Logging.getLogger(ExpressionWidget.class);

    private IMap map;

    public ExpressionWidget(IMap map) {
        this.map = map;
    }

    public void create(final Composite parent, final int style,
            final Map<String, Object> processParams, final Parameter<?> param,
            final Map<Widget, Map<String, Object>> uiParams) {
        composite = new Composite(parent, style);
        composite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 1, 1));

        GridLayout layout = new GridLayout(2, false);
        layout.marginTop = 0;
        layout.marginBottom = 0;
        layout.marginLeft = 0;
        layout.marginRight = 0;
        layout.horizontalSpacing = 0;
        layout.verticalSpacing = 0;
        layout.marginWidth = 0;
        layout.marginHeight = 0;
        composite.setLayout(layout);

        final Combo cboField = widget.createCombo(composite, 1, false);
        cboField.setData(param.key);
        if (param.sample != null) {
            cboField.add(param.sample.toString());
            cboField.setText(param.sample.toString());
        }

        Map<String, Object> metadata = param.metadata;
        if (metadata != null && metadata.containsKey(Params.FIELD)) {
            uiParams.put(cboField, metadata);
        }

        final Color oldBackColor = cboField.getBackground();
        cboField.addModifyListener(new ModifyListener() {
            @Override
            public void modifyText(ModifyEvent e) {
                if (cboField.getText().length() == 0) {
                    cboField.setBackground(oldBackColor);
                    processParams.put(param.key, null);
                } else {
                    try {
                        Expression expression = ECQL.toExpression(cboField.getText());
                        processParams.put(param.key, expression);
                        cboField.setBackground(oldBackColor);
                    } catch (CQLException e1) {
                        processParams.put(param.key, null);
                        cboField.setBackground(warningColor);
                    }
                }
            }
        });

        Button btnOpen = widget.createButton(composite, null, null, 1);
        Image helpImage = ToolboxPlugin.getImageDescriptor("icons/help.gif").createImage(); //$NON-NLS-1$
        btnOpen.setImage(helpImage);
        btnOpen.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent event) {
                ExpressionBuilderDialog dialog = new ExpressionBuilderDialog(parent.getShell(),
                        map, cboField.getText());
                dialog.setBlockOnOpen(true);
                if (dialog.open() == Window.OK) {
                    cboField.add(dialog.getSelectedValues());
                    cboField.setText(dialog.getSelectedValues());
                }
            }
        });

        composite.pack();
    }
}
