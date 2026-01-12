package com.jipple.sql.catalyst.plans.logical;

import com.jipple.sql.catalyst.expressions.Expression;
import com.jipple.sql.catalyst.expressions.named.NamedExpression;

import java.util.List;

public class Project extends UnaryNode {
    public final List<Expression> projectList;

    public Project(List<Expression> projectList, LogicalPlan child) {
        super(child);
        for (Expression expression : projectList) {
            if (!(expression instanceof NamedExpression)) {
                throw new IllegalArgumentException("Project expr must be NamedExpression");
            }
        }
        this.projectList = projectList;
    }

    @Override
    public Object[] args() {
        return new Object[] { projectList, child };
    }

    @Override
    public LogicalPlan withNewChildInternal(LogicalPlan newChild) {
        return new Project(projectList, newChild);
    }
}
