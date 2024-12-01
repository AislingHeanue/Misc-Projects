use std::{
    fmt::{Display, Formatter},
    result::Result,
};

pub trait Evaluate {
    type Output;
    fn eval(&self, x: &f32, y: &f32) -> Result<Self::Output, &'static str>;
}

pub enum Node {
    Numeric(NodeNumeric),
    Boolean(NodeBoolean),
    Colour(NodeColour),
}

pub struct NodeColour {
    pub r: Box<NodeNumeric>,
    pub g: Box<NodeNumeric>,
    pub b: Box<NodeNumeric>,
}

pub enum NodeNumeric {
    Number(f32),
    Add(Box<NodeNumeric>, Box<NodeNumeric>),
    Mult(Box<NodeNumeric>, Box<NodeNumeric>),
    X,
    Y,
    If(Box<NodeBoolean>, Box<NodeNumeric>, Box<NodeNumeric>),
    RandomNumber,
}

pub enum NodeBoolean {
    GreaterThan(Box<NodeNumeric>, Box<NodeNumeric>),
    Equal(Box<NodeNumeric>, Box<NodeNumeric>),
}

impl Display for NodeColour {
    fn fmt(&self, f: &mut Formatter<'_>) -> std::fmt::Result {
        write!(f, "Colour({}, {}, {})", self.r, self.g, self.b)
    }
}

impl Display for NodeNumeric {
    fn fmt(&self, f: &mut Formatter<'_>) -> std::fmt::Result {
        match self {
            NodeNumeric::Number(num) => write!(f, "{}", num),
            NodeNumeric::Add(first, second) => write!(f, "({} + {})", first, second),
            NodeNumeric::Mult(first, second) => write!(f, "({} * {})", first, second),
            NodeNumeric::X => write!(f, "x"),
            NodeNumeric::Y => write!(f, "y"),
            NodeNumeric::If(cond, then, other) => {
                write!(f, "If ({}) then ({}) else ({})", cond, then, other)
            }
            NodeNumeric::RandomNumber => panic!("Random Number should not be printed"),
        }
    }
}

impl Display for NodeBoolean {
    fn fmt(&self, f: &mut Formatter<'_>) -> std::fmt::Result {
        match self {
            NodeBoolean::GreaterThan(first, second) => write!(f, "{} > {}", first, second),
            NodeBoolean::Equal(first, second) => write!(f, "{} == {}", first, second),
        }
    }
}

impl Evaluate for NodeColour {
    type Output = (u8, u8, u8);
    fn eval(&self, x: &f32, y: &f32) -> Result<(u8, u8, u8), &'static str> {
        let r = to_u8(self.r.eval(x, y)?)?;
        let g = to_u8(self.g.eval(x, y)?)?;
        let b = to_u8(self.b.eval(x, y)?)?;
        Ok((r, g, b))
    }
}

impl Evaluate for NodeNumeric {
    type Output = f32;
    fn eval(&self, x: &f32, y: &f32) -> Result<f32, &'static str> {
        match self {
            NodeNumeric::Number(num) => Ok(*num),
            NodeNumeric::Add(first, second) => {
                Ok(((first.eval(x, y)? + second.eval(x, y)? + 1.0) % 2.0) - 1.0)
            }
            NodeNumeric::Mult(first, second) => {
                Ok(((first.eval(x, y)? * second.eval(x, y)? + 1.0) % 2.0) - 1.0)
            }
            NodeNumeric::X => Ok(*x),
            NodeNumeric::Y => Ok(*y),
            NodeNumeric::If(cond, then, other) => {
                if cond.eval(x, y)? {
                    then.eval(x, y)
                } else {
                    other.eval(x, y)
                }
            }
            NodeNumeric::RandomNumber => Err("Random number must not be evaluated"),
        }
    }
}

impl Evaluate for NodeBoolean {
    type Output = bool;
    fn eval(&self, x: &f32, y: &f32) -> Result<bool, &'static str> {
        match self {
            NodeBoolean::GreaterThan(first, second) => Ok(first.eval(x, y)? > second.eval(x, y)?),
            NodeBoolean::Equal(first, second) => Ok(first.eval(x, y)? == second.eval(x, y)?),
        }
    }
}

fn to_u8(i: f32) -> Result<u8, &'static str> {
    if i < -1.0 || i > 1.0 {
        return Err("x must be between -1 and 1");
    }
    Ok(((i + 1.0) * 128.0) as u8)
}
