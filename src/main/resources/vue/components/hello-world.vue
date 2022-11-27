<template id="hello-world">
  <header>
  <h1 class="hello-world">Health Tracker</h1>
  <form
    method="get"
    action="/api/users/logout"
  >
  <button>Logout</button>
  </form>
  </header>
  <h3 class="title">Users</h3>
  <table>
    <tr>
        <th>id</th>
        <th>name</th>
        <th>email</th>
    </tr>
    <tr v-for="user in users" :key="user.id">
        <td>{{user.id}}</td>
        <td>{{user.name}}</td>
        <td>{{user.email}}</td>
    </tr>
  </table>

  <h3 class="title">Activities</h3>
    <table>
      <tr>
          <th>id</th>
          <th>name</th>
          <th>calories</th>
      </tr>
      <tr v-for="activity in activities" :key="activity.id">
          <td>{{activity.id}}</td>
          <td>{{activity.activityName}}</td>
          <td>{{activity.calories}}</td>
      </tr>
    </table>

</template>
<script>
  app.component("hello-world", {
    template: "#hello-world",
     data: () => ({
        users: null,
        activities:null,
     }),
     created() {
         fetch(`/api/users`)
            .then(res => res.json())
            .then(json => {
                console.log(json)
                this.users = json
            })
            .catch(() => alert("Error while fetching user"));
         fetch(`/api/activities`)
            .then(res => res.json())
            .then(json => {
                console.log(json)
                this.activities = json
            })
            .catch(() => alert("Error while fetching activities"));
     }
   });
</script>
<style>
  header{
    display:flex;
    flex-direction:row;
    align-items:center;
    justify-content:center;
  }
  .hello-world {
    color: goldenrod;
    text-align:center;
    margin-left:auto;
    margin-right:auto;
  }
  .title{
    color: grey;
    text-align:center;
  }
  table, th, td {
    border:1px solid black;
  }
  table{
    width:100%;
  }
</style>