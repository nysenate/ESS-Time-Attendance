<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<section class="content-container">
    <h1>Grant New Supervisor Privileges</h1>
    <p class="content-info">
        Grant a person privileges to review and/or approve your employee's time records.
    </p>
    <div style="padding:10px;width:720px;margin:auto;">
        <form action="">
            <div style="display:inline-block;vertical-align: top;">
                <label>Choose Grantee</label>
                <select style="width:200px;margin-left:20px;">
                    <option>Grantee #1</option>
                    <option>Grantee #2</option>
                    <option>Grantee #3</option>
                    <option>Grantee #4</option>
                </select><br/><br/>
                <label>Start Date</label>
                <input style="margin-left:60px;" type="text" datepicker/><br/><br/>
                <label>End Date</label>
                <input style="margin-left:64px;"type="text" datepicker/>
            </div>
            <div style="display:inline-block;vertical-align: top;margin-left:20px;width:330px;">
                <label>Employee Listing</label><br/>
                <select multiple>
                    <option>Employee 1 FullName</option>
                    <option>Employee 2 FullName</option>
                    <option>Employee 3 FullName</option>
                    <option>Employee 4 FullName</option>
                    <option>Employee 5 FullName</option>
                </select>
            </div>

            <input type="button" class="neutral-button" value="Reset"/>
            <input type="button" class="submit-button" value="Grant"/>

        </form>
    </div>
</section>

<section class="content-container">
    <h1>Privileges Granted By You</h1>
    <p class="content-info">
        A listing of the current privileges that you have granted to another employee.
    </p>
</section>

<section class="content-container">
    <h1>Privileges Granted To You</h1>
    <p class="content-info">
        A listing of the current privileges that have been granted to you.
    </p>
</section>