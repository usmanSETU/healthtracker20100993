<template id="login">
<form
  id="login-form"
  action="/api/users/login"
  method="post"
>
    <div class="login">
        <div class="login-card">
            <h1>Login</h1>
            <label for="email">Email</label>
            <input v-model="email" required type="email" name="email" placeholder="Enter Email" id="email">
            <label for="password">Password</label>
            <input v-model="password" required type="password" name="password" placeholder="Enter Password" id="password">
            <button type="submit">Login</button>
        </div>
    </div>
    </form>
</template>

<script>
    // noinspection JSAnnotator
    app.component("Login",{
        template:'#login',
        data:()=>({
            email:"",
            password:""
        }),
        methods:{
            async submit(){
                const data = await fetch('/login',{
                    method:"post",
                    body:JSON.stringify({email:this.email,password:this.password})
                });
                console.log(data);
                if(data.redirected === true)
                    console.log(window);
            }
        }
    });
</script>

<style>
    .login{
        height:100%;
        width:100%;
        display:flex;
        justify-content:center;
        align-items:center;

    }
    .login label{
        align-self:start;
    }
    .login .login-card{
        display:flex;
        flex-direction:column;
        justify-content:center;
        align-items:center;
        padding:50px;
        background-color:#92b6ee;
        border-radius:25px;
        width:50%;
    }

    .login .login-card h1{
        text-align:center;
    }

    .login .login-card input{
        border:none;
        border-radius: 10px;
        margin:10px 0 10px 0;
        height:30px;
        padding-left:10px;
        width:100%;
    }

    .login .login-card button{
        width:25%;
        background-color:#3478e3;
        color:white;
        padding:10px;
        border:none;
        border-radius:10px;
    }
    .login .login-card button:hover{
        color:#3478e3;
        background-color:white;
    }
</style>
