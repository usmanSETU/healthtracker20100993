<template id="signup">
<form
  id="signup-form"
  action="/api/users"
  method="post"
>
    <div class="signup">
        <div class="signup-card">
            <h1>Sign Up</h1>
            <label for="name">Name</label>
            <input v-model="name" required type="text" name="name" :placeholder="">
            <label for="email">Email</label>
            <input v-model="email" required type="email" name="email" :placeholder="">
            <label for="password">Password</label>
            <input v-model="password" required type="password" name="password" :placeholder="">
            <button type="submit">signup</button>
        </div>
    </div>
    </form>
</template>

<script>
    app.component("signup",{
        template:"#signup",
        data:()=>({
            email:"",
            password:"",
            name:""
        }),
        methods:{

        }
    });
</script>

<style>
    .signup{
        height:100%;
        width:100%;
        background-color:white;
        display:flex;
        justify-content:center;
        align-items:center;
    }
    .signup label{
        align-self:start;
    }
    .signup .signup-card{
        display:flex;
        flex-direction:column;
        justify-content:center;
        align-items:center;
        padding:50px;
        background-color:#92b6ee;
        border-radius:25px;
        width:50%;
    }

    .signup .signup-card h1{
        text-align:center;
    }

    .signup .signup-card input{
        border:none;
        border-radius: 10px;
        margin:10px 0 10px 0;
        height:30px;
        padding-left:10px;
        width:100%;
    }

    .signup .signup-card button{
        width:25%;
        background-color:#3478e3;
        color:white;
        padding:10px;
        border:none;
        border-radius:10px;
    }
    .signup .signup-card button:hover{
        color:#3478e3;
        background-color:white;
    }
</style>
