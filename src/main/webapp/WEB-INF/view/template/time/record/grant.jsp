<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<div ng-controller="GrantPrivilegesCtrl">
    <div class="content-container content-controls">
        <p class="content-info">Grant another supervisor privileges to review and/or approve your direct employee's time records.</p>
        <div class="padding-10">
            <table class="simple-table">
                <thead>
                <tr>
                    <th>#</th>
                    <th>Supervisor</th>
                    <th>Status</th>
                    <th>Start Date</th>
                    <th>End Date</th>
                </tr>
                </thead>
                <tbody>
                <tr ng-repeat="grantee in state.grantees">
                    <td>{{$index + 1}}</td>
                    <td>{{grantee.firstName}} {{grantee.lastName}}</td>
                    <td>
                        <div class="horizontal-input-group">
                            <input type="checkbox" id="grant-status-yes-{{$index}}" ng-model="grantee.granted"
                                   ng-value="true" name="grant-status[{{$index}}]"/>
                            <label ng-class="{'success-bold-label': grantee.granted === true}"
                                   for="grant-status-yes-{{$index}}">Grant Access</label>
                        </div>
                    </td>
                    <td ng-class="{'half-opacity': grantee.granted === false}">
                        <div class="horizontal-input-group">
                            <input id="grant-start-date-{{$index}}" ng-checked="grantee.grantStart"
                                   ng-disabled="grantee.granted === false" type="checkbox" ng-click="setStartDate(grantee)"/>
                            <label for="grant-start-date-{{$index}}">Set Start Date</label>
                            <input ng-class="{'half-opacity': !grantee.granted || !grantee.grantStart}"
                                   ng-disabled="!grantee.granted || !grantee.grantStart" ng-model="grantee.grantStart"
                                   style="width:100px" type="text" datepicker/>
                        </div>
                    </td>
                    <td ng-class="{'half-opacity': grantee.granted === false}">
                        <div class="horizontal-input-group">
                            <input id="grant-end-date-{{$index}}" ng-checked="grantee.grantEnd"
                                   ng-disabled="grantee.granted === false" type="checkbox" ng-click="setStartDate(grantee)"/>
                            <label for="grant-end-date-{{$index}}">Set End Date</label>
                            <input ng-disabled="!grantee.granted || !grantee.grantEnd" ng-model="grantee.grantEnd"
                                   style="width:100px" type="text" datepicker/>
                        </div>
                    </td>
                </tr>
                </tbody>
            </table>
            <hr/>
            <div class="content-info" style="text-align: center;">
                <input type="button" class="neutral-button" value="Discard Changes" ng-click="reset()"/>
                <input type="button" class="submit-button" value="Update Grant Privileges"/>
            </div>
        </div>
        <div style="display:none" class="grid">
            <div class="col-1-2 padding-10">
                <form action="">
                    <div style="display:inline-block;vertical-align: top;">
                        <label>Choose Grantee</label>
                        <select ng-model="state.selectedGrantee"
                                ng-options="grantee.fullName for grantee in state.grantees">
                        </select>
                        <br/><br/>
                        <label>End Date</label>
                        <input style="margin-left:64px;"type="text" datepicker/>
                    </div>
                </form>
            </div>
            <div class="col-1-2 padding-10">
                <label>Select Employees</label><hr/>
                <input type="checkbox" value="All"/><label>Select all employees</label>
            </div>
        </div>
    </div>


        <%--<div class="content-container">--%>
        <%--<h1>Grant New Supervisor Privileges</h1>--%>

    </div>
</div>
